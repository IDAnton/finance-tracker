package ru.ivanov.financetracker.dto;

import java.math.BigDecimal;


public record BalanceResponseDto(
    Long userId,
    BigDecimal balance
) {}
