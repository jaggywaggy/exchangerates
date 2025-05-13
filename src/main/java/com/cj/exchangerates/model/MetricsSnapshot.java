package com.cj.exchangerates.model;

import java.util.Map;

public class MetricsSnapshot {
    private int totalQueries;
    private Map<String, ApiMetrics> apis;

    public MetricsSnapshot() {
    }

    public MetricsSnapshot(int totalQueries,
                           Map<String, ApiMetrics> apis) {
        this.totalQueries = totalQueries;
        this.apis = apis;
    }

    public int getTotalQueries() {
        return this.totalQueries;
    }

    public void setTotalQueries(int totalQueries) {
        this.totalQueries = totalQueries;
    }

    public Map<String, ApiMetrics> getApis() {
        return this.apis;
    }

    public void setApis(Map<String, ApiMetrics> apis) {
        this.apis = apis;
    }
}
