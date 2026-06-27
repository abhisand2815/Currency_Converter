package com.currencyapp.model;

import java.time.LocalDate;

public class RateSnapshot {
    private final LocalDate date;
    private final double rate;

    public RateSnapshot(LocalDate date, double rate) {
        this.date = date;
        this.rate = rate;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getRate() {
        return rate;
    }
}
