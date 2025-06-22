package com.cj.exchangerates.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cj.exchangerates.model.ExchangeRateResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service implementation for external API interactions.
 * 
 * This service handles communication with multiple external exchange rate APIs,
 * providing a unified interface for fetching rates from different sources.
 * It implements comprehensive error handling and metrics tracking.
 */
@Service
@Qualifier("ApiService")
public class ApiService implements IApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private IMetricsService metricsService;
    
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Fetches exchange rates from all configured external APIs.
     * 
     * This method coordinates calls to multiple external APIs and returns
     * a map of responses keyed by API name. Each API call is tracked for
     * metrics and error handling.
     *
     * @param base    The base currency code
     * @param symbols List of target currency codes
     * @return Map of API responses keyed by API name
     */
    @Override
    public Map<String, ExchangeRateResponse> fetchAllRates(String base, List<String> symbols) {
        logger.debug("Fetching rates from all external APIs - Base: {}, Symbols: {}", base, symbols);
        
        final Map<String, ExchangeRateResponse> allRates = new HashMap<>();
        
        try {
            // Fetch from Frankfurter API
            ExchangeRateResponse frankfurterRates = fetchFrankfurter(base, symbols);
            if (frankfurterRates != null) {
                allRates.put("frankfurter", frankfurterRates);
                logger.debug("Successfully fetched rates from Frankfurter API");
            }
        } catch (Exception e) {
            logger.error("Error fetching rates from Frankfurter API", e);
        }
        
        try {
            // Fetch from Fawaz API
            ExchangeRateResponse fawazRates = fetchFawaz(base, symbols);
            if (fawazRates != null) {
                allRates.put("fawaz", fawazRates);
                logger.debug("Successfully fetched rates from Fawaz API");
            }
        } catch (Exception e) {
            logger.error("Error fetching rates from Fawaz API", e);
        }
        
        logger.info("Completed fetching rates from {} APIs", allRates.size());
        return allRates;
    }
    
    /**
     * Fetches current exchange rates from Frankfurter API.
     * 
     * Frankfurter API provides European Central Bank rates and is known for
     * reliability and accuracy. The API returns rates in a standardized format.
     *
     * @param base    The base currency code
     * @param symbols List of target currency codes
     * @return ExchangeRateResponse containing the rates, or null if failed
     */
    private ExchangeRateResponse fetchFrankfurter(String base, List<String> symbols) {
        final String url = UriComponentsBuilder.fromUriString("https://api.frankfurter.app/latest")
                                               .queryParam("from", base)
                                               .queryParam("to", String.join(",", symbols))
                                               .toUriString();

        logger.debug("Fetching from Frankfurter API: {}", url);
        metricsService.incrementRequest("frankfurter");

        try {
            // GET request to Frankfurter API
            @SuppressWarnings("unchecked")
            final Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                logger.warn("Null response received from Frankfurter API");
                return null;
            }

            metricsService.incrementResponse("frankfurter");
            logger.debug("Received response from Frankfurter API");

            // Extract rates from response
            final Object ratesObj = response.get("rates");
            if (ratesObj instanceof Map<?, ?>) {
                final Map<String, Double> rates = objectMapper.convertValue(ratesObj, 
                    new TypeReference<Map<String, Double>>() {});
                
                if (rates != null && !rates.isEmpty()) {
                    logger.debug("Successfully parsed {} rates from Frankfurter API", rates.size());
                    return new ExchangeRateResponse(base, rates);
                }
            }

            logger.warn("Invalid rates format in Frankfurter API response");
            return null;

        } catch (RestClientException e) {
            logger.error("HTTP error when calling Frankfurter API", e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error when calling Frankfurter API", e);
            return null;
        }
    }

    /**
     * Fetches current exchange rates from Fawazahmed0/currency-api.
     * 
     * This API provides community-driven exchange rates and serves as a
     * backup source for rate data. The response format differs from Frankfurter.
     *
     * @param base    The base currency code
     * @param symbols List of target currency codes
     * @return ExchangeRateResponse containing the rates, or null if failed
     */
    private ExchangeRateResponse fetchFawaz(String base, List<String> symbols) {
        final String url = String.format(
            "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/%s.json", 
            base.toLowerCase()
        );

        logger.debug("Fetching from Fawaz API: {}", url);
        metricsService.incrementRequest("fawaz");

        try {
            // GET request to Fawaz API
            @SuppressWarnings("unchecked")
            final Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey(base.toLowerCase())) {
                logger.warn("Invalid response from Fawaz API - missing base currency data");
                return null;
            }

            metricsService.incrementResponse("fawaz");
            logger.debug("Received response from Fawaz API");
            
            // Extract rates from nested response structure
            final Map<String, Double> rates = new HashMap<>();
            final Object nestedRates = response.get(base.toLowerCase());
            
            if (nestedRates instanceof Map<?, ?> ratesObjMap) {
                for (String symbol : symbols) {
                    final Object ratesObj = ratesObjMap.get(symbol.toLowerCase());
                    if (ratesObj instanceof Number result) {
                        rates.put(symbol.toUpperCase(), result.doubleValue());
                    }
                }
            }
            
            if (!rates.isEmpty()) {
                logger.debug("Successfully parsed {} rates from Fawaz API", rates.size());
                return new ExchangeRateResponse(base.toUpperCase(), rates);
            }

            logger.warn("No valid rates found in Fawaz API response");
            return null;

        } catch (RestClientException e) {
            logger.error("HTTP error when calling Fawaz API", e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error when calling Fawaz API", e);
            return null;
        }
    }
}
