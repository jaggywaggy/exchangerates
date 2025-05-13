package com.cj.exchangerates.service;

import java.util.List;
import java.util.Map;

import com.cj.exchangerates.model.ExchangeRateResponse;

public interface IApiService {
    /**
     * Fetch all rates from both API sources.
     * @param currency  The currency.
     * @param symbols   The symbols.
     * @return          The rates mapped by rate source.
     */
    public Map<String, ExchangeRateResponse> fetchAllRates(String currency, List<String> symbols);
}
