package com.cj.exchangerates.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cj.exchangerates.model.ExchangeRateResponse;

/**
 * Service implementation for exchange rate operations.
 * 
 * This service provides the core business logic for retrieving and processing
 * exchange rates from multiple external APIs. It implements intelligent caching,
 * rate averaging, and comprehensive metrics tracking.
 */
@Service
@Qualifier("ExchangeRateService")
public class ExchangeRateService implements IExchangeRateService {
	
	private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
	
	@Autowired
	private IApiService apiService;
	
	@Autowired
	private IMetricsService metricsService;
	
	/**
	 * Thread-safe cache for storing exchange rate responses.
	 * Key format: {base}|{sorted_symbols} (e.g., "USD|EUR,GBP,JPY")
	 */
	private final Map<String, ExchangeRateResponse> cache = new ConcurrentHashMap<>();

	/**
	 * Retrieves exchange rates for the specified base currency and target symbols.
	 * 
	 * This method implements the following workflow:
	 * 1. Validates input parameters
	 * 2. Checks cache for existing results
	 * 3. Fetches rates from multiple external APIs
	 * 4. Averages the rates for improved accuracy
	 * 5. Caches the result for future requests
	 * 6. Tracks metrics for monitoring
	 *
	 * @param base    The base currency code (must not be null or empty)
	 * @param symbols List of target currency codes (must not be null or empty)
	 * @return ExchangeRateResponse containing averaged rates
	 * @throws IllegalArgumentException if base or symbols are invalid
	 */
	@Override
	public ExchangeRateResponse getExchangeRates(String base, List<String> symbols) {
	    logger.debug("Processing exchange rate request - Base: {}, Symbols: {}", base, symbols);
	    
	    // Input validation
	    validateInput(base, symbols);
	    
	    // Increment metrics
	    metricsService.incrementTotalQueries();
	    
	    // Generate cache key
	    final String cacheKey = createCacheKey(base, symbols);
	    
	    // Check cache first
	    if (cache.containsKey(cacheKey)) {
	        logger.debug("Cache hit for key: {}", cacheKey);
	        return cache.get(cacheKey);
	    }
	    
	    logger.debug("Cache miss for key: {}, fetching from external APIs", cacheKey);
	    
	    // Fetch rates from all external APIs
	    final Map<String, ExchangeRateResponse> allRates = apiService.fetchAllRates(base, symbols);
	    final Map<String, List<Double>> combinedRates = new HashMap<>();
	    
	    // Combine rates from all API sources
	    for (Map.Entry<String, ExchangeRateResponse> entry : allRates.entrySet()) {
	        String apiName = entry.getKey();
	        ExchangeRateResponse rateResponse = entry.getValue();
	        
	        if (rateResponse != null && rateResponse.getRates() != null) {
	            logger.debug("Processing rates from API: {}", apiName);
	            for (Map.Entry<String, Double> rateEntry : rateResponse.getRates().entrySet()) {
	                combinedRates.computeIfAbsent(rateEntry.getKey(), k -> new ArrayList<>())
	                            .add(rateEntry.getValue());
	            }
	        } else {
	            logger.warn("No valid rates received from API: {}", apiName);
	        }
	    }
	    
	    // Calculate averages across all rate responses
	    final Map<String, Double> averagedRates = calculateAverages(combinedRates);
	    
	    // Create and cache the result
	    final ExchangeRateResponse result = new ExchangeRateResponse(base.toUpperCase(), averagedRates);
	    cache.put(cacheKey, result);
	    
	    logger.info("Exchange rates calculated and cached - Base: {}, Symbols: {}, Rates count: {}", 
	               base, symbols, averagedRates.size());
	    
	    return result;
    }
	
	/**
	 * Validates input parameters for exchange rate requests.
	 *
	 * @param base    The base currency to validate
	 * @param symbols The list of symbols to validate
	 * @throws IllegalArgumentException if validation fails
	 */
	private void validateInput(String base, List<String> symbols) {
	    if (base == null || base.trim().isEmpty()) {
	        logger.warn("Invalid base currency: {}", base);
	        throw new IllegalArgumentException("Base currency must be provided");
	    }
	    if (symbols == null || symbols.isEmpty()) {
	        logger.warn("Invalid symbols list: {}", symbols);
	        throw new IllegalArgumentException("At least one target currency symbol must be provided");
	    }
	    
	    // Validate individual symbols
	    for (String symbol : symbols) {
	        if (symbol == null || symbol.trim().isEmpty()) {
	            logger.warn("Invalid symbol in list: {}", symbol);
	            throw new IllegalArgumentException("Currency symbols cannot be null or empty");
	        }
	    }
	}
	
	/**
	 * Calculates average rates from combined rate data.
	 *
	 * @param combinedRates Map of currency codes to lists of rates
	 * @return Map of currency codes to averaged rates
	 */
	private Map<String, Double> calculateAverages(Map<String, List<Double>> combinedRates) {
	    final Map<String, Double> averagedRates = new HashMap<>();
	    
	    for (Map.Entry<String, List<Double>> entry : combinedRates.entrySet()) {
	        String currency = entry.getKey();
	        List<Double> rates = entry.getValue();
	        
	        if (!rates.isEmpty()) {
	            double sum = rates.stream().mapToDouble(Double::doubleValue).sum();
	            double average = sum / rates.size();
	            averagedRates.put(currency, average);
	            
	            logger.debug("Calculated average for {}: {} (from {} sources)", currency, average, rates.size());
	        } else {
	            logger.warn("No rates available for currency: {}", currency);
	        }
	    }
	    
	    return averagedRates;
	}
	
	/**
	 * Creates a cache key from base currency and symbols.
	 * The key format ensures consistent caching regardless of symbol order.
	 *
	 * @param base    The base currency
	 * @param symbols The list of symbols
	 * @return A consistent cache key string
	 */
    private String createCacheKey(String base, List<String> symbols) {
	    final List<String> sorted = new ArrayList<>(symbols);
	    Collections.sort(sorted);
	    return base.toUpperCase() + "|" + String.join(",", sorted).toUpperCase();
	}
}
