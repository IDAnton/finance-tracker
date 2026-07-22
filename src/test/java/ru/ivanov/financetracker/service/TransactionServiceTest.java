package ru.ivanov.financetracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ivanov.financetracker.model.Transaction;
import ru.ivanov.financetracker.model.User;
import ru.ivanov.financetracker.repository.TransactionRepository;
import ru.ivanov.financetracker.repository.UserRepository;
import ru.ivanov.financetracker.utils.TestDtoCreator;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private User testUser;

    @InjectMocks
    private TransactionService transactionService;

    private TestDtoCreator dtoCreator;

    private Long userId;
    private String userName = "test";

    @BeforeEach
    void setUp() {
        userId = 1L;
        userName = "test";
        dtoCreator = new TestDtoCreator();
    }

    @Test
    void createTransaction_Success(){
        when(testUser.getId()).thenReturn(userId);
        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(testUser));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction incomingTransaction = invocation.getArgument(0);
            incomingTransaction.setId(0L);
            return incomingTransaction;
        });


        var transactionDtoLists = dtoCreator.createRandomTransactionDtoLists(1, userId);
        var createDto = transactionDtoLists.createList().getFirst();
        var responseDto = transactionDtoLists.responseList().getFirst();

        var createdDto =  transactionService.createTransaction(createDto, userName);

        assertEquals(createdDto, responseDto);

    }
}
