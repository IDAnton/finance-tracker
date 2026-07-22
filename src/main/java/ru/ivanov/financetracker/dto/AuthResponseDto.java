package ru.ivanov.financetracker.dto;

public record AuthResponseDto(
        String token,
        String username,
        Long userId
) {}
