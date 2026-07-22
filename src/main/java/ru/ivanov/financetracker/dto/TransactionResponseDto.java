package ru.ivanov.financetracker.dto;


import ru.ivanov.financetracker.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDto (
        Long id,
        BigDecimal amount,
        String category,
        TransactionType type,
        String description,
        LocalDateTime transactionDate,
        Long userId
)
{}
