package com.cj.exchangerates.model;

import java.util.List;

/**
 * A class representing a snapshot of metrics data.
 * 
 */
public class MetricsSnapshot {
	private int totalQueries;
    private List<ApiMetricsWithName> apis;

    public MetricsSnapshot() {
    }

    public MetricsSnapshot(int totalQueries, List<ApiMetricsWithName> apis) {
        this.totalQueries = totalQueries;
        this.apis = apis;
    }

    public int getTotalQueries() {
        return totalQueries;
    }

    public void setTotalQueries(int totalQueries) {
        this.totalQueries = totalQueries;
    }

    public List<ApiMetricsWithName> getApis() {
        return apis;
    }

    public void setApis(List<ApiMetricsWithName> apis) {
        this.apis = apis;
    }
    
    /**
     * Nested metrics object to track the API name and metrics.
     * 
     */
    public static class ApiMetricsWithName {
        private String name;
        private ApiMetrics metrics;

        public ApiMetricsWithName() {}

        public ApiMetricsWithName(String name, ApiMetrics metrics) {
            this.name = name;
            this.metrics = metrics;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ApiMetrics getMetrics() {
            return metrics;
        }

        public void setMetrics(ApiMetrics metrics) {
            this.metrics = metrics;
        }
    }
}
