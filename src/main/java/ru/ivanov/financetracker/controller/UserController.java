package ru.ivanov.financetracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ivanov.financetracker.dto.AuthResponseDto;
import ru.ivanov.financetracker.dto.UserLoginDto;
import ru.ivanov.financetracker.dto.UserRegisterDto;
import ru.ivanov.financetracker.dto.UserResponseDto;
import ru.ivanov.financetracker.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegisterDto dto){
        UserResponseDto response = userService.registerUser(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody UserLoginDto dto){
        AuthResponseDto response = userService.loginUser(dto);
        return ResponseEntity.ok(response);
    }
}
