package com.cj.exchangerates.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cj.exchangerates.model.ExchangeRateResponse;
import com.cj.exchangerates.service.IExchangeRateService;

/**
 * REST controller for exchange rate operations.
 * Provides endpoints for retrieving exchange rates from multiple sources.
 */
@RestController
@RequestMapping("/exchangeRates")
public class ExchangeRateController {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateController.class);

    @Autowired
    private IExchangeRateService exchangeRateService;

    /**
     * Retrieves exchange rates for the specified base currency and target symbols.
     * 
     * This endpoint aggregates rates from multiple external APIs and returns
     * averaged rates for improved accuracy and reliability.
     *
     * @param base    The base currency code (3-letter ISO code, e.g., USD, EUR, GBP)
     * @param symbols List of target currency codes (3-letter ISO codes)
     * @return ExchangeRateResponse containing the base currency and averaged rates
     * 
     * @example GET /exchangeRates/USD?symbols=EUR,GBP,JPY
     * @example Response: {"base": "USD", "rates": {"EUR": 0.85, "GBP": 0.73, "JPY": 110.25}}
     */
    @GetMapping("/{base}")
    public ResponseEntity<ExchangeRateResponse> getExchangeRates(
            @PathVariable String base,
            @RequestParam List<String> symbols) {
        
        logger.info("Exchange rate request received - Base: {}, Symbols: {}", base, symbols);
        
        try {
            ExchangeRateResponse response = exchangeRateService.getExchangeRates(base, symbols);
            logger.info("Exchange rate response generated successfully for base: {}", base);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request parameters - Base: {}, Symbols: {}, Error: {}", base, symbols, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error processing exchange rate request - Base: {}, Symbols: {}", base, symbols, e);
            throw e;
        }
    }
}
