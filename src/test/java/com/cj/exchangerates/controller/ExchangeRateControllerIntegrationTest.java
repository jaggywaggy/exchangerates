package com.cj.exchangerates.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cj.exchangerates.model.ExchangeRateResponse;
import com.cj.exchangerates.service.IExchangeRateService;

/**
 * Integration tests for ExchangeRateController.
 * Tests the complete request flow including validation and error handling.
 */
@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IExchangeRateService exchangeRateService;

    private ExchangeRateResponse mockResponse;

    @BeforeEach
    void setUp() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        rates.put("GBP", 0.73);
        rates.put("JPY", 110.25);
        
        mockResponse = new ExchangeRateResponse("USD", rates);
    }

    @Test
    void testGetExchangeRates_Success() throws Exception {
        when(exchangeRateService.getExchangeRates(anyString(), anyList()))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/exchangeRates/USD")
                .param("symbols", "EUR", "GBP", "JPY")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.base").value("USD"))
                .andExpect(jsonPath("$.rates.EUR").value(0.85))
                .andExpect(jsonPath("$.rates.GBP").value(0.73))
                .andExpect(jsonPath("$.rates.JPY").value(110.25));
    }

    @Test
    void testGetExchangeRates_WithSingleSymbol() throws Exception {
        Map<String, Double> singleRate = new HashMap<>();
        singleRate.put("EUR", 0.85);
        ExchangeRateResponse singleResponse = new ExchangeRateResponse("USD", singleRate);
        
        when(exchangeRateService.getExchangeRates("USD", List.of("EUR")))
            .thenReturn(singleResponse);

        mockMvc.perform(get("/exchangeRates/USD")
                .param("symbols", "EUR")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base").value("USD"))
                .andExpect(jsonPath("$.rates.EUR").value(0.85));
    }

    @Test
    void testGetExchangeRates_ServiceThrowsException() throws Exception {
        when(exchangeRateService.getExchangeRates(anyString(), anyList()))
            .thenThrow(new IllegalArgumentException("Invalid currency code"));

        mockMvc.perform(get("/exchangeRates/INVALID")
                .param("symbols", "EUR", "GBP")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"))
                .andExpect(jsonPath("$.message").value("Invalid currency code"));
    }

    @Test
    void testGetExchangeRates_MissingSymbols() throws Exception {
        mockMvc.perform(get("/exchangeRates/USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetExchangeRates_EmptySymbols() throws Exception {
        mockMvc.perform(get("/exchangeRates/USD")
                .param("symbols", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
} 