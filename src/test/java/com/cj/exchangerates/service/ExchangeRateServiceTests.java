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
    void testCachedResult() {
        final List<String> symbols = List.of("USD", "NZD");
        final String currency = "EUR";
        
        final Map<String, ExchangeRateResponse> mockRates = new HashMap<>();
        mockRates.put("frankfurter", new ExchangeRateResponse(currency, Map.of("USD", 1.0, "NZD", 1.5)));
        mockRates.put("fawaz", new ExchangeRateResponse(currency, Map.of("USD", 1.2, "NZD", 1.7)));
        when(_apiService.fetchAllRates(currency, symbols)).thenReturn(mockRates);

        // First call will load our data into our cache.
        final ExchangeRateResponse first = _service.getExchangeRates(currency, symbols);
        Assertions.assertEquals(1.1, first.getRates().get("USD"), 0.0001);

        // Second call will load our data from the cache directly.
        final ExchangeRateResponse second = _service.getExchangeRates(currency, symbols);
        Assertions.assertEquals(first, second);

        // We should have only called fetchAllRates once as the second getExchangeRates
        // call should have returned our cached data.
        verify(_apiService, times(1)).fetchAllRates(currency, symbols);
    }
}