package com.cj.exchangerates.model;

public class ApiMetrics {
    private int totalRequests;
    private int totalResponses;

    public ApiMetrics() {
    }

    public ApiMetrics(int totalRequests,
                      int totalResponses) {
        this.totalRequests = totalRequests;
        this.totalResponses = totalResponses;
    }

    public int getTotalRequests() {
        return this.totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getTotalResponses() {
        return this.totalResponses;
    }

    public void setTotalResponses(int totalResponses) {
        this.totalResponses = totalResponses;
    }
}
