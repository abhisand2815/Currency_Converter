package com.currencyapp.model;

import java.time.LocalDateTime;

public class ConversionResult {
    private final Currency fromCurrency;
    private final Currency toCurrency;
    private final double amount;
    private final double rate;
    private final double resultAmount;
    private final LocalDateTime timestamp;

    public ConversionResult(Currency fromCurrency, Currency toCurrency, double amount, double rate, double resultAmount, LocalDateTime timestamp) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.rate = rate;
        this.resultAmount = resultAmount;
        this.timestamp = timestamp;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public double getAmount() {
        return amount;
    }

    public double getRate() {
        return rate;
    }

    public double getResultAmount() {
        return resultAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
