package com.cj.exchangerates.service;

import java.util.List;
import java.util.Map;

import com.cj.exchangerates.model.ExchangeRateResponse;

public interface IApiService {
    /**
     * Fetch all rates from both API sources.
     * @param base  	The base.
     * @param symbols   The symbols.
     * @return          The rates mapped by rate source.
     */
    public Map<String, ExchangeRateResponse> fetchAllRates(String base, List<String> symbols);
}
