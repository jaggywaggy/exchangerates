package com.cj.exchangerates.service;

import com.cj.exchangerates.model.MetricsSnapshot;

public interface IMetricsService {

    /**
     * Increments the count of total exchange queries.
     * 
     */
    public void incrementTotalQueries();
    
    /**
     * Increments the number of requests sent to the given API.
     * 
     * @param api The name of the API.
     */
    public void incrementRequest(String api);

    /**
     * Increments the number of responses received from the given API.
     *
     * @param api The name of the API.
     */
    public void incrementResponse(String api);

    /**
     * Returns a snapshot of current metrics.
     *
     * @return The current metrics snapshot
     */
    public MetricsSnapshot getSnapshot();
}
