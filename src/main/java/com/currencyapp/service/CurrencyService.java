package com.currencyapp.service;

import com.currencyapp.model.Currency;
import com.currencyapp.model.RateSnapshot;
import com.currencyapp.util.ConfigLoader;
import com.currencyapp.util.JsonHelper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyService {
    private static CurrencyService instance;

    private final HttpClient httpClient;
    private final String apiUrl;
    private final String apiKey; // Loaded from config, though v4 doesn't require it

    private final List<Currency> currencies;
    private final Map<String, JsonHelper.ApiResponse> cache = new ConcurrentHashMap<>();
    
    private boolean isOnline = false;
    private LocalDateTime lastUpdated = null;

    // Listeners for connectivity or updates
    private final List<Runnable> statusListeners = new ArrayList<>();

    public interface RateCallback {
        void onResult(Map<String, Double> rates, LocalDateTime timestamp, boolean isLive);
        void onError(String message);
    }

    private CurrencyService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        
        // Load API settings
        this.apiUrl = ConfigLoader.getProperty("api.url", "https://api.exchangerate-api.com/v4/latest/");
        this.apiKey = ConfigLoader.getProperty("api.key", "");

        // Load static currencies
        this.currencies = JsonHelper.loadCurrenciesFromResources();

        // Seed initial offline fallback rates in cache for USD
        initializeFallbackRates();
    }

    public static synchronized CurrencyService getInstance() {
        if (instance == null) {
            instance = new CurrencyService();
        }
        return instance;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void addStatusListener(Runnable listener) {
        synchronized (statusListeners) {
            statusListeners.add(listener);
        }
    }

    private void notifyStatusChanged() {
        synchronized (statusListeners) {
            for (Runnable listener : statusListeners) {
                try {
                    listener.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initializeFallbackRates() {
        // Essential default rates to ensure the app has some logical rates even if launched completely offline
        JsonHelper.ApiResponse usdFallback = new JsonHelper.ApiResponse();
        usdFallback.base = "USD";
        usdFallback.date = LocalDate.now().toString();
        usdFallback.time_last_updated = Instant.now().getEpochSecond();
        usdFallback.rates = new HashMap<>();
        
        // Setup rough offline defaults for primary currencies
        usdFallback.rates.put("USD", 1.0);
        usdFallback.rates.put("EUR", 0.92);
        usdFallback.rates.put("GBP", 0.79);
        usdFallback.rates.put("INR", 83.50);
        usdFallback.rates.put("JPY", 155.0);
        usdFallback.rates.put("CAD", 1.36);
        usdFallback.rates.put("AUD", 1.50);
        usdFallback.rates.put("CNY", 7.25);
        usdFallback.rates.put("CHF", 0.89);
        usdFallback.rates.put("SGD", 1.35);

        // Fill remaining currencies with 1.0 so we don't NullPointer for non-seeded ones
        for (Currency c : currencies) {
            usdFallback.rates.putIfAbsent(c.getCode(), 1.0);
        }

        cache.put("USD", usdFallback);
        lastUpdated = LocalDateTime.now();
    }

    /**
     * Asynchronously fetches rates for base currency. Fallback to cache/derivation if offline.
     */
    public void getRates(String baseCode, RateCallback callback) {
        String urlString = apiUrl + baseCode;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return response.body();
                    } else {
                        throw new RuntimeException("API error: HTTP " + response.statusCode());
                    }
                })
                .thenAccept(body -> {
                    JsonHelper.ApiResponse response = JsonHelper.parseRatesResponse(body);
                    if (response != null && response.rates != null) {
                        cache.put(baseCode, response);
                        isOnline = true;
                        lastUpdated = LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(response.time_last_updated), 
                                ZoneId.systemDefault()
                        );
                        notifyStatusChanged();
                        callback.onResult(response.rates, lastUpdated, true);
                    } else {
                        throw new RuntimeException("Failed to parse rates");
                    }
                })
                .exceptionally(ex -> {
                    // Offline fallback
                    isOnline = false;
                    notifyStatusChanged();

                    // Check if we have this exact base in cache
                    JsonHelper.ApiResponse cached = cache.get(baseCode);
                    if (cached != null) {
                        LocalDateTime cacheTime = LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(cached.time_last_updated), 
                                ZoneId.systemDefault()
                        );
                        callback.onResult(cached.rates, cacheTime, false);
                    } else {
                        // Try to derive rates from any cached currency (typically USD)
                        Map<String, Double> derivedRates = deriveRates(baseCode);
                        if (derivedRates != null) {
                            callback.onResult(derivedRates, lastUpdated, false);
                        } else {
                            callback.onError("No rates available. Please check internet connection.");
                        }
                    }
                    return null;
                });
    }

    /**
     * Derives conversion rates relative to baseCode using some other cached currency rates.
     * E.g. If we only have USD rates: Rate(A->B) = Rate(USD->B) / Rate(USD->A)
     */
    private Map<String, Double> deriveRates(String targetBaseCode) {
        // Find any cached base
        for (Map.Entry<String, JsonHelper.ApiResponse> entry : cache.entrySet()) {
            String cachedBase = entry.getKey();
            JsonHelper.ApiResponse cachedData = entry.getValue();

            // We need cachedBase's rate to targetBaseCode to invert it
            Double baseToTargetRate = cachedData.rates.get(targetBaseCode);
            if (baseToTargetRate != null && baseToTargetRate > 0) {
                Map<String, Double> derived = new HashMap<>();
                for (Map.Entry<String, Double> rateEntry : cachedData.rates.entrySet()) {
                    String currencyCode = rateEntry.getKey();
                    double rateFromBase = rateEntry.getValue();
                    // New rate = (Cached -> TargetCurrency) / (Cached -> TargetBase)
                    derived.put(currencyCode, rateFromBase / baseToTargetRate);
                }
                return derived;
            }
        }
        return null;
    }

    /**
     * Simulates 7-day history trend for a pair of currencies using a seeded random variance.
     */
    public List<RateSnapshot> getSimulatedHistory(String fromCode, String toCode, double currentRate) {
        List<RateSnapshot> snapshots = new ArrayList<>();
        
        // Generate a seed based on the codes and rate to keep the chart consistent
        long seed = (fromCode + toCode).hashCode() + (long)(currentRate * 12345.67);
        Random rand = new Random(seed);

        double rate = currentRate;
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            snapshots.add(0, new RateSnapshot(date, rate));

            // Variance: ±0.5%
            double variance = (rand.nextDouble() - 0.5) * 0.01;
            rate = rate * (1 + variance);
        }

        return snapshots;
    }

    /**
     * Checks internet connectivity by executing a quick check.
     */
    public CompletableFuture<Boolean> checkConnectivity() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.exchangerate-api.com"))
                .timeout(Duration.ofSeconds(1))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(response -> {
                    isOnline = true;
                    notifyStatusChanged();
                    return true;
                })
                .exceptionally(ex -> {
                    isOnline = false;
                    notifyStatusChanged();
                    return false;
                });
    }
}
