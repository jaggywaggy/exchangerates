package com.cj.exchangerates.service;

import java.util.List;

import com.cj.exchangerates.model.ExchangeRateResponse;

public interface IExchangeRateService {

    /**
     * Calls our exchange rates API endpoint.
     * 
     * @param base      The base currency.
     * @param symbols   The symbols.
     * @return          The exchange rates response.
     */
    public ExchangeRateResponse getExchangeRates(String currency, List<String> symbols);

}
