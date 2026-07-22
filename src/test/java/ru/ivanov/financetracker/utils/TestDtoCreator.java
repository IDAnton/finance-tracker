package ru.ivanov.financetracker.utils;

import org.springframework.stereotype.Component;
import ru.ivanov.financetracker.dto.TransactionCreateDto;
import ru.ivanov.financetracker.dto.TransactionResponseDto;
import ru.ivanov.financetracker.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TestDtoCreator {
    public record pairOfTransactionsDtoLists<T1, T2>(List<T1> createList, List<T2> responseList) {}

    public pairOfTransactionsDtoLists<TransactionCreateDto, TransactionResponseDto> createRandomTransactionDtoLists(int numberOfTransactions, Long userId) {
        List<TransactionCreateDto> createList = new ArrayList<>(numberOfTransactions);
        List<TransactionResponseDto> responseList = new ArrayList<>(numberOfTransactions);
        List<TransactionType> transactionTypes = Arrays.asList(TransactionType.values());
        for(int i = 0; i < numberOfTransactions; i++){
            BigDecimal randomAmount = (new BigDecimal("1000"))
                    .multiply(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble()));
            TransactionType randomType = transactionTypes.get(ThreadLocalRandom.current().nextInt(transactionTypes.size()));

            TransactionCreateDto create = new TransactionCreateDto(randomAmount, "test", randomType, "test_description");
            LocalDateTime time = null;
            TransactionResponseDto response = new TransactionResponseDto((long) i, randomAmount, "test", randomType, "test_description", time, userId);
            createList.add(create);
            responseList.add(response);
        }
        return new pairOfTransactionsDtoLists<>(createList, responseList);
    }
}
