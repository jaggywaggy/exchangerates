package com.cj.exchangerates.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
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
    }
}