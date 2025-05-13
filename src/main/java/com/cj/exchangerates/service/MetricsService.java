package com.cj.exchangerates.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cj.exchangerates.model.ApiMetrics;
import com.cj.exchangerates.model.MetricsSnapshot;


@Service
@Qualifier("MetricsService")
public class MetricsService implements IMetricsService {
    
    private final AtomicInteger _totalQueries = new AtomicInteger();
    private final Map<String, AtomicInteger> _requests = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> _responses = new ConcurrentHashMap<>();

    @Override
    public void incrementTotalQueries() {
        _totalQueries.incrementAndGet();
    }

    @Override
    public void incrementRequest(String api) {
        _requests.computeIfAbsent(api, a -> new AtomicInteger()).incrementAndGet();
    }

    @Override
    public void incrementResponse(String api) {
        _responses.computeIfAbsent(api, a -> new AtomicInteger()).incrementAndGet();
    }

    @Override
    public MetricsSnapshot getSnapshot() {
    	final List<MetricsSnapshot.ApiMetricsWithName> metricsList = new ArrayList<>();
        for (String name : _requests.keySet()) {
            final int requests = _requests.get(name).get();
            final int responses = _responses.getOrDefault(name, new AtomicInteger()).get();
            final ApiMetrics metrics = new ApiMetrics(requests, responses);
            metricsList.add(new MetricsSnapshot.ApiMetricsWithName(name, metrics));
        }
        return new MetricsSnapshot(_totalQueries.get(), metricsList);
    }
}
