package com.cj.exchangerates.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cj.exchangerates.model.ExchangeRateResponse;

public class ExchangeRateServiceTests {

    @InjectMocks
    private ExchangeRateService _service;

    @Mock
    private IApiService _apiService;

    @Mock
    private MetricsService _metricsService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testGetExchangeRates() {
        final List<String> symbols = List.of("USD", "NZD");
        final String base = "EUR";
        
        // Mock.
        final Map<String, ExchangeRateResponse> mockRates = new HashMap<>();
        mockRates.put("frankfurter", new ExchangeRateResponse(base, new HashMap<>()));
        mockRates.put("fawaz", new ExchangeRateResponse(base, Map.of("USD", 1.2, "NZD", 1.7)));
        when(_apiService.fetchAllRates(base, symbols)).thenReturn(mockRates);

        final ExchangeRateResponse response = _service.getExchangeRates(base, symbols);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1.2, response.getRates().get("USD"), 0.0001);
        Assertions.assertEquals(1.7, response.getRates().get("NZD"), 0.0001);
    }

    @Test
    void testCachedResult() {
        final List<String> symbols = List.of("USD", "NZD");
        final String base = "EUR";
        
        // Mock.
        final Map<String, ExchangeRateResponse> mockRates = new HashMap<>();
        mockRates.put("frankfurter", new ExchangeRateResponse(base, Map.of("USD", 1.0, "NZD", 1.5)));
        mockRates.put("fawaz", new ExchangeRateResponse(base, Map.of("USD", 1.2, "NZD", 1.7)));
        when(_apiService.fetchAllRates(base, symbols)).thenReturn(mockRates);

        // First call will load our data into our cache.
        final ExchangeRateResponse first = _service.getExchangeRates(base, symbols);
        Assertions.assertNotNull(first);
        Assertions.assertEquals(1.1, first.getRates().get("USD"), 0.0001);

        // Second call will load our data from the cache directly.
        final ExchangeRateResponse second = _service.getExchangeRates(base, symbols);
        Assertions.assertNotNull(second);
        Assertions.assertEquals(first, second);

        // We should have only called fetchAllRates once as the second getExchangeRates
        // call should have returned our cached data.
        verify(_apiService, times(1)).fetchAllRates(base, symbols);
    }
    
    @Test
    void testAveraging() {
    	final List<String> symbols = List.of("USD", "NZD");
    	final String base = "EUR";
    	
    	// Mock.
        final Map<String, ExchangeRateResponse> mockRates = new HashMap<>();
        mockRates.put("frankfurter", new ExchangeRateResponse(base, Map.of("USD", 1.0, "NZD", 1.5)));
        mockRates.put("fawaz", new ExchangeRateResponse(base, Map.of("USD", 1.2, "NZD", 1.7)));
        when(_apiService.fetchAllRates(base, symbols)).thenReturn(mockRates);
        
        final ExchangeRateResponse response = _service.getExchangeRates(base, symbols);
        
        Assertions.assertNotNull(response);
        Assertions.assertEquals("EUR", response.getBase());
        Assertions.assertEquals(1.1, response.getRates().get("USD"), 0.0001);
        Assertions.assertEquals(1.6, response.getRates().get("NZD"), 0.0001);

        verify(_metricsService).incrementTotalQueries();
    }
    
    @Test
    void testValidation() {
    	// Missing base.
    	Assertions.assertThrows(IllegalArgumentException.class, () -> _service.getExchangeRates(null, List.of("USD", "NZD")));
    	// Missing symbols.
    	Assertions.assertThrows(IllegalArgumentException.class, () -> _service.getExchangeRates("EUR", null));
    }
}