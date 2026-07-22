package ru.ivanov.financetracker.dto;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDto (
    @NotBlank(message = "имя должно быть не пустым")
    String username,
    @NotBlank(message = "пароль должен быть не пустым")
    String password
)
{}
