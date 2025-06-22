package com.cj.exchangerates.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cj.exchangerates.model.ApiMetrics;
import com.cj.exchangerates.model.MetricsSnapshot;
import com.cj.exchangerates.service.MetricsService;

/**
 * Integration tests for MetricsController.
 * Tests the metrics endpoint functionality.
 */
@WebMvcTest(MetricsController.class)
class MetricsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetricsService metricsService;

    private MetricsSnapshot mockSnapshot;

    @BeforeEach
    void setUp() {
        List<MetricsSnapshot.ApiMetricsWithName> apiMetrics = new ArrayList<>();
        
        ApiMetrics frankfurterMetrics = new ApiMetrics(50, 48);
        apiMetrics.add(new MetricsSnapshot.ApiMetricsWithName("frankfurter", frankfurterMetrics));
        
        ApiMetrics fawazMetrics = new ApiMetrics(50, 49);
        apiMetrics.add(new MetricsSnapshot.ApiMetricsWithName("fawaz", fawazMetrics));
        
        mockSnapshot = new MetricsSnapshot(100, apiMetrics);
    }

    @Test
    void testGetMetrics_Success() throws Exception {
        when(metricsService.getSnapshot()).thenReturn(mockSnapshot);

        mockMvc.perform(get("/metrics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalQueries").value(100))
                .andExpect(jsonPath("$.apis").isArray())
                .andExpect(jsonPath("$.apis.length()").value(2))
                .andExpect(jsonPath("$.apis[0].name").value("frankfurter"))
                .andExpect(jsonPath("$.apis[0].metrics.totalRequests").value(50))
                .andExpect(jsonPath("$.apis[0].metrics.totalResponses").value(48))
                .andExpect(jsonPath("$.apis[1].name").value("fawaz"))
                .andExpect(jsonPath("$.apis[1].metrics.totalRequests").value(50))
                .andExpect(jsonPath("$.apis[1].metrics.totalResponses").value(49));
    }

    @Test
    void testGetMetrics_EmptyMetrics() throws Exception {
        MetricsSnapshot emptySnapshot = new MetricsSnapshot(0, new ArrayList<>());
        when(metricsService.getSnapshot()).thenReturn(emptySnapshot);

        mockMvc.perform(get("/metrics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQueries").value(0))
                .andExpect(jsonPath("$.apis").isArray())
                .andExpect(jsonPath("$.apis.length()").value(0));
    }

    @Test
    void testGetMetrics_ServiceThrowsException() throws Exception {
        when(metricsService.getSnapshot()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/metrics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }
} 