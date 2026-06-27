package com.currencyapp.util;

import com.currencyapp.model.Currency;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class JsonHelper {
    private static final Gson gson = new Gson();

    public static class ApiResponse {
        public String base;
        public String date;
        public long time_last_updated;
        public Map<String, Double> rates;
    }

    public static List<Currency> loadCurrenciesFromResources() {
        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(JsonHelper.class.getResourceAsStream("/currencies.json"), "currencies.json not found"))) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> currencyMap = gson.fromJson(reader, type);
            List<Currency> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : currencyMap.entrySet()) {
                list.add(new Currency(entry.getKey(), entry.getValue()));
            }
            Collections.sort(list);
            return list;
        } catch (Exception e) {
            System.err.println("Error loading currencies.json: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static ApiResponse parseRatesResponse(String jsonResponse) {
        try {
            return gson.fromJson(jsonResponse, ApiResponse.class);
        } catch (Exception e) {
            System.err.println("Error parsing API rates: " + e.getMessage());
            return null;
        }
    }
}
