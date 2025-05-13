package com.cj.exchangerates.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cj.exchangerates.model.ExchangeRateResponse;

@Service
@Qualifier("ExchangeRateService")
public class ExchangeRateService implements IExchangeRateService {
	
	@Autowired
	private IApiService _apiService;
	@Autowired
	private IMetricsService _metricsService;
	
	private final Map<String, ExchangeRateResponse> _cache = new ConcurrentHashMap<>();

	@Override
	public ExchangeRateResponse getExchangeRates(String base, List<String> symbols) {
	    if(base == null || base.isEmpty()) {
	        throw new IllegalArgumentException("Base must be provided");
	    }
	    if(symbols == null || symbols.isEmpty()) {
	        throw new IllegalArgumentException("Symbols must be provided");
	    }
	    
	    // Increment our total queries after validation.
	    _metricsService.incrementTotalQueries();
	    
	    // Generate a key for caching.
	    final String cacheKey = createCacheKey(base, symbols);
	    if(_cache.containsKey(cacheKey)) {
	        return _cache.get(cacheKey);
	    }
	    
	    final Map<String, ExchangeRateResponse> allRates = _apiService.fetchAllRates(base, symbols);
	    final Map<String, List<Double>> combinedRates = new HashMap<>();
	    
	    // Do a pass through, combining our response rates from all API
	    // sources.
	    for(ExchangeRateResponse rateResponse : allRates.values()) {
	        final Map<String, Double> rates = rateResponse.getRates();
	        if(rates != null && !rates.isEmpty()) {
	            for(Map.Entry<String, Double> entry : rates.entrySet()) {
                    combinedRates.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
	            }
	        }
	    }
	    
	    // Finally, calculate our averages across all rate responses.
	    final Map<String, Double> averagedRates = new HashMap<>();
	    for(Map.Entry<String, List<Double>> entry : combinedRates.entrySet()) {
	        double sum = 0.0;
	        for(Double rate : entry.getValue()) {
	            sum += rate;
	        }
	        final double average = entry.getValue().isEmpty() ? 0.0 : sum / entry.getValue().size();
	        averagedRates.put(entry.getKey(), average);    
	    }

	    final ExchangeRateResponse result = new ExchangeRateResponse(base.toUpperCase(), averagedRates);
	    _cache.put(cacheKey, result);
	    return result;
    }
	
    private String createCacheKey(String base, List<String> symbols) {
	    final List<String> sorted = new ArrayList<>(symbols);
	    Collections.sort(sorted);
	    return base + "|" + String.join(",", sorted).toUpperCase();
	}

}
