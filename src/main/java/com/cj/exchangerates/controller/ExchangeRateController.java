package com.cj.exchangerates.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cj.exchangerates.model.ExchangeRateResponse;
import com.cj.exchangerates.service.IExchangeRateService;

@RestController
@RequestMapping("/exchangeRates")
public class ExchangeRateController {

    @Autowired
    private IExchangeRateService _service;

    /**
     * Endpoint for fetching exchange rates.
     *
     * @param base		The base currency.
     * @param symbols 	The symbols.
     * @return 			Averaged exchange rates from multiple sources.
     */
    @GetMapping("/{base}")
    public ExchangeRateResponse getExchangeRates(@PathVariable String base,
    											 @RequestParam List<String> symbols) {
        return _service.getExchangeRates(base, symbols);
    }
}
