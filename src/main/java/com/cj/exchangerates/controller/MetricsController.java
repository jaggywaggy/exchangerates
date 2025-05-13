package com.cj.exchangerates.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cj.exchangerates.model.MetricsSnapshot;
import com.cj.exchangerates.service.MetricsService;

@RestController
public class MetricsController {

    @Autowired
    private MetricsService _metricsService;

    @GetMapping("/metrics")
    public MetricsSnapshot getMetrics() {
        return _metricsService.getSnapshot();
    }
}
