package com.cj.exchangerates.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cj.exchangerates.model.ApiMetrics;
import com.cj.exchangerates.model.MetricsSnapshot;

/**
 * Service implementation for application metrics tracking.
 * 
 * This service provides thread-safe metrics collection and reporting capabilities.
 * It tracks total queries, API requests, and responses using atomic counters
 * for concurrent access safety.
 */
@Service
@Qualifier("MetricsService")
public class MetricsService implements IMetricsService {
    
    private static final Logger logger = LoggerFactory.getLogger(MetricsService.class);
    
    /**
     * Atomic counter for total exchange rate queries processed.
     */
    private final AtomicInteger totalQueries = new AtomicInteger();
    
    /**
     * Thread-safe map tracking request counts per API.
     */
    private final Map<String, AtomicInteger> requests = new ConcurrentHashMap<>();
    
    /**
     * Thread-safe map tracking response counts per API.
     */
    private final Map<String, AtomicInteger> responses = new ConcurrentHashMap<>();

    /**
     * Increments the total number of exchange rate queries processed.
     * This method is thread-safe using atomic operations.
     */
    @Override
    public void incrementTotalQueries() {
        int currentCount = totalQueries.incrementAndGet();
        logger.debug("Total queries incremented to: {}", currentCount);
    }

    /**
     * Increments the request count for a specific API.
     * This method is thread-safe and will create a new counter if one doesn't exist.
     *
     * @param api The name of the API (e.g., "frankfurter", "fawaz")
     */
    @Override
    public void incrementRequest(String api) {
        AtomicInteger counter = requests.computeIfAbsent(api, a -> new AtomicInteger());
        int currentCount = counter.incrementAndGet();
        logger.debug("Request count for {} incremented to: {}", api, currentCount);
    }

    /**
     * Increments the response count for a specific API.
     * This method is thread-safe and will create a new counter if one doesn't exist.
     *
     * @param api The name of the API (e.g., "frankfurter", "fawaz")
     */
    @Override
    public void incrementResponse(String api) {
        AtomicInteger counter = responses.computeIfAbsent(api, a -> new AtomicInteger());
        int currentCount = counter.incrementAndGet();
        logger.debug("Response count for {} incremented to: {}", api, currentCount);
    }

    /**
     * Generates a snapshot of current metrics.
     * 
     * This method creates a thread-safe snapshot of all current metrics,
     * including total queries and per-API request/response statistics.
     *
     * @return MetricsSnapshot containing current application metrics
     */
    @Override
    public MetricsSnapshot getSnapshot() {
        final int totalQueriesCount = totalQueries.get();
        final List<MetricsSnapshot.ApiMetricsWithName> metricsList = new ArrayList<>();
        
        // Build metrics for each API
        for (String apiName : requests.keySet()) {
            final int requestCount = requests.get(apiName).get();
            final int responseCount = responses.getOrDefault(apiName, new AtomicInteger()).get();
            final ApiMetrics metrics = new ApiMetrics(requestCount, responseCount);
            metricsList.add(new MetricsSnapshot.ApiMetricsWithName(apiName, metrics));
            
            logger.debug("API {} metrics - Requests: {}, Responses: {}", 
                        apiName, requestCount, responseCount);
        }
        
        MetricsSnapshot snapshot = new MetricsSnapshot(totalQueriesCount, metricsList);
        logger.info("Generated metrics snapshot - Total queries: {}, APIs tracked: {}", 
                   totalQueriesCount, metricsList.size());
        
        return snapshot;
    }
}
