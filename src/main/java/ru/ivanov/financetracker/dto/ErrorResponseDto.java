package ru.ivanov.financetracker.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String errorCode,
        String message,
        LocalDateTime timestamp
) {}
