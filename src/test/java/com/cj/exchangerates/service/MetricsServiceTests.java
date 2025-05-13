package com.cj.exchangerates.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.cj.exchangerates.model.MetricsSnapshot;
import com.cj.exchangerates.model.MetricsSnapshot.ApiMetricsWithName;

public class MetricsServiceTests {

    @Mock
    private MetricsService _metricsService;

    @BeforeEach
    void init() {
    	_metricsService = new MetricsService();
    }
    
    @Test
    void testIncrementTotalQueries() {
    	_metricsService.incrementTotalQueries();
    	_metricsService.incrementTotalQueries();
    	_metricsService.incrementTotalQueries();
    	final MetricsSnapshot snapshot = _metricsService.getSnapshot();
    	Assertions.assertNotNull(snapshot);
    	Assertions.assertEquals(3, snapshot.getTotalQueries());
    }
    
    @Test
    void testIncrementRequests() {
    	final String API_NAME_1 = "API 1";
    	final String API_NAME_2 = "API 2";
    	_metricsService.incrementRequest(API_NAME_1);
    	_metricsService.incrementRequest(API_NAME_1);
    	_metricsService.incrementRequest(API_NAME_2);
    	final MetricsSnapshot snapshot = _metricsService.getSnapshot();
    	Assertions.assertNotNull(snapshot);
    	final List<ApiMetricsWithName> metrics = snapshot.getApis();
    	Assertions.assertNotNull(metrics);
    	final ApiMetricsWithName metric1 = metrics.stream().filter(m -> m.getName().equals(API_NAME_1)).findAny().orElseThrow();
    	Assertions.assertNotNull(metric1);
    	final ApiMetricsWithName metric2 = metrics.stream().filter(m -> m.getName().equals(API_NAME_2)).findAny().orElseThrow();
    	Assertions.assertNotNull(metric2);
    	Assertions.assertEquals(2, metric1.getMetrics().getTotalRequests());
    	Assertions.assertEquals(1, metric2.getMetrics().getTotalRequests());
    }

    @Test
    void testIncrementResponses() {
    	final String API_NAME_1 = "API 1";
    	final String API_NAME_2 = "API 2";
    	// Init.
    	_metricsService.incrementRequest(API_NAME_1);
    	_metricsService.incrementRequest(API_NAME_2);
    	// Record responses.
    	_metricsService.incrementResponse(API_NAME_1);
    	_metricsService.incrementResponse(API_NAME_2);
    	_metricsService.incrementResponse(API_NAME_2);
    	final MetricsSnapshot snapshot = _metricsService.getSnapshot();
    	Assertions.assertNotNull(snapshot);
    	final List<ApiMetricsWithName> metrics = snapshot.getApis();
    	Assertions.assertNotNull(metrics);
    	final ApiMetricsWithName metric1 = metrics.stream().filter(m -> m.getName().equals(API_NAME_1)).findAny().orElseThrow();
    	Assertions.assertNotNull(metric1);
    	final ApiMetricsWithName metric2 = metrics.stream().filter(m -> m.getName().equals(API_NAME_2)).findAny().orElseThrow();
    	Assertions.assertNotNull(metric2);
    	Assertions.assertEquals(1, metric1.getMetrics().getTotalResponses());
    	Assertions.assertEquals(2, metric2.getMetrics().getTotalResponses());
    }
    
}