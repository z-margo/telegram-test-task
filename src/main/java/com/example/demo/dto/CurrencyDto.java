package com.example.demo.dto;

import java.math.BigDecimal;

public record CurrencyDto(
        String symbol,
        BigDecimal price
) {
}
