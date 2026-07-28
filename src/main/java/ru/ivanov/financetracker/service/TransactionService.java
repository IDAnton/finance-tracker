package ru.ivanov.financetracker.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ivanov.financetracker.dto.BalanceResponseDto;
import ru.ivanov.financetracker.dto.TransactionCreateDto;
import ru.ivanov.financetracker.dto.TransactionResponseDto;
import ru.ivanov.financetracker.exception.ResourceNotFoundException;
import ru.ivanov.financetracker.model.Transaction;
import ru.ivanov.financetracker.model.User;
import ru.ivanov.financetracker.repository.TransactionRepository;
import ru.ivanov.financetracker.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponseDto createTransaction(TransactionCreateDto dto, String username){

        log.info("Создание транзакции для : {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("пользователь " + username + " не найден"));

        Transaction transaction = Transaction.builder()
                .amount(dto.amount())
                .category(dto.category())
                .type(dto.type())
                .user(user)
                .description(dto.description())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Транзакция id: {} для пользователя: {} сохранена", transaction.getId(), username);

        return mapToResponseDto(savedTransaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getUserTransactions(String username){

        log.info("Запрос транзакций для пользователя: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("пользователь " + username + " не найден"));
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(user.getId())
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public BalanceResponseDto calculateUserTotalBalance(String username) {

        log.info("Запрос на расчет баланса для пользователя: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("пользователь " + username + " не найден"));

        BigDecimal balance = transactionRepository.calculateBalanceByUserId(user.getId());
        return new BalanceResponseDto(user.getId(), balance);
    }

    private TransactionResponseDto mapToResponseDto(Transaction transaction) {
        return new TransactionResponseDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getType(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getUser().getId());
    }
}
