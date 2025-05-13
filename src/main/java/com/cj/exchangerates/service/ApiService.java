package com.cj.exchangerates.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cj.exchangerates.model.ExchangeRateResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Qualifier("ApiService")
public class ApiService implements IApiService {
    
    @Autowired
    private ObjectMapper _objectMapper;
	@Autowired
	private IMetricsService _metricsService;
    
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Map<String, ExchangeRateResponse> fetchAllRates(String currency, List<String> symbols) {
        final Map<String, ExchangeRateResponse> allRates = new HashMap<>();
        allRates.put("frankfurter", fetchFrankfurter(currency, symbols));
        allRates.put("fawaz", fetchFawaz(currency, symbols));
        return allRates;
    }
    
    /**
     * Fetch current rates from Frankfurter.
     * 
     */
    private ExchangeRateResponse fetchFrankfurter(String currency, List<String> symbols) {
        final String url = UriComponentsBuilder.fromUriString("https://api.frankfurter.app/latest")
                                               .queryParam("from", currency)
                                               .queryParam("to", String.join(",", symbols))
                                               .toUriString();

        _metricsService.incrementRequest("frankfurter");

        // GET request.
        @SuppressWarnings("unchecked")
        final Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        final Object ratesObj = response.get("rates");
        
        _metricsService.incrementResponse("frankfurter");

        // Check our response (mapped by rates)
        if(ratesObj instanceof Map<?, ?>) {
            final Map<String, Double> rates = _objectMapper.convertValue(ratesObj, new TypeReference<Map<String, Double>>() {});
            if(rates != null && !rates.isEmpty()) {
                return new ExchangeRateResponse(currency, rates);
            }
        }

        return null;
    }

    /**
     * Fetch current rates from fawazahmed0.
     * 
     */
    private ExchangeRateResponse fetchFawaz(String currency, List<String> symbols) {
        // One request per symbol so lets run this in parallel.
        final Map<String, Double> rates = symbols.parallelStream().map(symbol -> {
            final String url = String.format("https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/%s/%s.json", currency.toLowerCase(), symbol.toLowerCase());
            
            _metricsService.incrementRequest("fawaz");

            // GET request.
            @SuppressWarnings("unchecked")
            final Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            _metricsService.incrementResponse("fawaz");

            // Check our response (mapped by symbol).
            if(response != null && response.containsKey(symbol.toLowerCase())) {
                final Object rateObj = response.get(symbol.toLowerCase());
                if(rateObj instanceof Number) {
                    final Number rateNum = (Number)rateObj;
                    return Map.entry(symbol.toUpperCase(), rateNum.doubleValue());
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if(rates != null && !rates.isEmpty()) {
            return new ExchangeRateResponse(currency.toUpperCase(), rates);
        }

        return null;
    }

}
