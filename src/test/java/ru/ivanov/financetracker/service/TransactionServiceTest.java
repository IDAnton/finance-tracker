package ru.ivanov.financetracker.service;

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


    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "test";


    @Test
    void testCreateTransactionSuccess(){
        when(testUser.getId()).thenReturn(USER_ID);
        when(userRepository.findByUsername(USER_NAME)).thenReturn(Optional.of(testUser));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction incomingTransaction = invocation.getArgument(0);
            incomingTransaction.setId(0L);
            return incomingTransaction;
        });


        var transactionDtoLists = TestDtoCreator.createRandomTransactionDtoLists(1, USER_ID);
        var createDto = transactionDtoLists.createList().getFirst();
        var responseDto = transactionDtoLists.responseList().getFirst();

        var createdDto =  transactionService.createTransaction(createDto, USER_NAME);

        assertEquals(createdDto, responseDto);

    }
}
