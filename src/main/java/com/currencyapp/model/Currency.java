package com.currencyapp.model;

import java.util.Objects;

public class Currency implements Comparable<Currency> {
    private final String code;
    private final String name;

    public Currency(String code, String name) {
        this.code = Objects.requireNonNull(code, "Currency code cannot be null").toUpperCase();
        this.name = Objects.requireNonNull(name, "Currency name cannot be null");
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }

    @Override
    public int compareTo(Currency o) {
        return this.code.compareTo(o.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        Currency currency = (Currency) o;
        return code.equals(currency.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
