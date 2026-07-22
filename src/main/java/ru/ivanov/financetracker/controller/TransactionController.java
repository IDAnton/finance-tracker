package ru.ivanov.financetracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.ivanov.financetracker.dto.BalanceResponseDto;
import ru.ivanov.financetracker.dto.TransactionCreateDto;
import ru.ivanov.financetracker.dto.TransactionResponseDto;
import ru.ivanov.financetracker.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDto> create(@Valid @RequestBody TransactionCreateDto dto,
                                                         @AuthenticationPrincipal UserDetails userDetails){
        TransactionResponseDto response = transactionService.createTransaction(dto, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<TransactionResponseDto>> getByUserId(@AuthenticationPrincipal UserDetails userDetails){
        List<TransactionResponseDto> transactions = transactionService.getUserTransactions(userDetails.getUsername());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user/balance")
    public ResponseEntity<BalanceResponseDto> getBalance(@AuthenticationPrincipal UserDetails userDetails){
        BalanceResponseDto balance = transactionService.calculateUserTotalBalance(userDetails.getUsername());
        return ResponseEntity.ok(balance);
    }
}
