package com.cj.exchangerates.model;

import java.util.Map;

/**
 * A class responsible for handling exchange rate responses.
 * 
 */
public class ExchangeRateResponse {

    private String base;
    private Map<String, Double> rates;

    public ExchangeRateResponse() {
    }

    public ExchangeRateResponse(String base,
                                Map<String, Double> rates) {
        this.base = base;
        this.rates = rates;
    }

    public String getBase() {
        return this.base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, Double> getRates() {
        return this.rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}
