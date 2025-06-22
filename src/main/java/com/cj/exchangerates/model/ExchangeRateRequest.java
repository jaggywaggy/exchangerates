package com.cj.exchangerates.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for exchange rate requests.
 * Contains validation annotations for input validation.
 */
public class ExchangeRateRequest {

    @NotBlank(message = "Base currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Base currency must be a 3-letter currency code")
    private String base;

    @NotEmpty(message = "At least one target currency symbol is required")
    @Size(min = 1, max = 10, message = "Must specify between 1 and 10 currency symbols")
    private List<@Pattern(regexp = "^[A-Z]{3}$", message = "Currency symbols must be 3-letter currency codes") String> symbols;

    public ExchangeRateRequest() {
    }

    public ExchangeRateRequest(String base, List<String> symbols) {
        this.base = base;
        this.symbols = symbols;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        return "ExchangeRateRequest{" +
                "base='" + base + '\'' +
                ", symbols=" + symbols +
                '}';
    }
} 