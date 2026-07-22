package ru.ivanov.financetracker.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.ivanov.financetracker.model.TransactionType;

import java.math.BigDecimal;


public record TransactionCreateDto (
    @NotNull(message = "Сумма не должна быть пустой")
    @Positive
    BigDecimal amount,

    @NotBlank
    @Size(max = 50)
    String category,

    @NotNull (message = "Укажите тип транзакции (INCOME или EXPENSE)")
    TransactionType type,

    @Size(max = 255)
    String description
) {}
