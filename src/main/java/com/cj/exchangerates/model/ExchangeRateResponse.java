package com.cj.exchangerates.model;

import java.util.Map;

/**
 * A class responsible for handling exchange rate responses.
 * 
 */
public class ExchangeRateResponse {

    private String currency;
    private Map<String, Double> rates;

    public ExchangeRateResponse() {
    }

    public ExchangeRateResponse(String currency, Map<String, Double> rates) {
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Map<String, Double> getRates() {
        return this.rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}
