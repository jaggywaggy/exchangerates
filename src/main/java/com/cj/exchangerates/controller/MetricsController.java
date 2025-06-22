package com.cj.exchangerates.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cj.exchangerates.model.MetricsSnapshot;
import com.cj.exchangerates.service.MetricsService;

/**
 * REST controller for application metrics.
 * Provides endpoints for monitoring application performance and usage statistics.
 */
@RestController
public class MetricsController {

    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);

    @Autowired
    private MetricsService metricsService;

    /**
     * Retrieves current application metrics including total queries and API usage statistics.
     * 
     * This endpoint provides real-time insights into application performance,
     * including the number of exchange rate queries processed and the success
     * rates of external API calls.
     *
     * @return MetricsSnapshot containing current application metrics
     * 
     * @example GET /metrics
     * @example Response: {"totalQueries": 150, "apis": [{"name": "frankfurter", "metrics": {"totalRequests": 75, "totalResponses": 75}}]}
     */
    @GetMapping("/metrics")
    public ResponseEntity<MetricsSnapshot> getMetrics() {
        logger.debug("Metrics request received");
        
        try {
            MetricsSnapshot snapshot = metricsService.getSnapshot();
            logger.debug("Metrics snapshot generated successfully - Total queries: {}", snapshot.getTotalQueries());
            return ResponseEntity.ok(snapshot);
        } catch (Exception e) {
            logger.error("Error generating metrics snapshot", e);
            throw e;
        }
    }
}
