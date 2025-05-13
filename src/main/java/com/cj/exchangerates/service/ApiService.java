package com.cj.exchangerates.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, ExchangeRateResponse> fetchAllRates(String base, List<String> symbols) {
        final Map<String, ExchangeRateResponse> allRates = new HashMap<>();
        allRates.put("frankfurter", fetchFrankfurter(base, symbols));
        allRates.put("fawaz", fetchFawaz(base, symbols));
        return allRates;
    }
    
    /**
     * Fetch current rates from Frankfurter.
     * 
     */
    private ExchangeRateResponse fetchFrankfurter(String base, List<String> symbols) {
        final String url = UriComponentsBuilder.fromUriString("https://api.frankfurter.app/latest")
                                               .queryParam("from", base)
                                               .queryParam("to", String.join(",", symbols))
                                               .toUriString();

        _metricsService.incrementRequest("frankfurter");

        // GET request.
        @SuppressWarnings("unchecked")
        final Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if(response == null) {
        	return null;
        }

        _metricsService.incrementResponse("frankfurter");

        final Object ratesObj = response.get("rates");
        // Check our response (mapped by rates)
        if(ratesObj instanceof Map<?, ?>) {
            final Map<String, Double> rates = _objectMapper.convertValue(ratesObj, new TypeReference<Map<String, Double>>() {});
            if(rates != null && !rates.isEmpty()) {
                return new ExchangeRateResponse(base, rates);
            }
        }

        return null;
    }

    /**
     * Fetch current rates from fawazahmed0.
     * 
     */
    private ExchangeRateResponse fetchFawaz(String base, List<String> symbols) {
		final String url = String.format("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/%s.json", base.toLowerCase());            

		_metricsService.incrementRequest("fawaz");

		// GET request.
		@SuppressWarnings("unchecked")
		final Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if(response == null || !response.containsKey(base.toLowerCase())) {
        	return null;
        }

		_metricsService.incrementResponse("fawaz");
		
		// Grab our exchange rates.
		final Map<String, Double> rates = new HashMap<>();
		final Object nestedRates = response.get(base.toLowerCase());
		if(nestedRates instanceof Map<?, ?> ratesObjMap) {
			for(String symbol : symbols) {
				final Object ratesObj = ratesObjMap.get(symbol.toLowerCase());
				if(ratesObj instanceof Number result) {
					rates.put(symbol.toUpperCase(), result.doubleValue());
				}
			}
		}
		
		if(!rates.isEmpty()) {
			return new ExchangeRateResponse(base.toUpperCase(), rates);
		}

        return null;
    }

}
