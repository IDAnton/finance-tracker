package ru.ivanov.financetracker.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;


import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.financetracker.dto.AuthResponseDto;
import ru.ivanov.financetracker.dto.UserRegisterDto;
import ru.ivanov.financetracker.dto.UserLoginDto;
import ru.ivanov.financetracker.model.User;
import ru.ivanov.financetracker.repository.UserRepository;
import ru.ivanov.financetracker.security.SecurityConfig;
import ru.ivanov.financetracker.utils.TestDtoCreator;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_USER = "test_user";
    private static final String TEST_EMAIL = "test@mail.com";
    private static final String TEST_PASSWORD = "qwerty123";


    @Test
    void testRegisterUserSuccess() throws Exception{
        var userRegisterDto = UserRegisterDto.builder()
                .username(TEST_USER).email(TEST_EMAIL).password(TEST_PASSWORD).build();

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value(TEST_USER))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
        boolean userExists = userRepository.existsByEmail(TEST_EMAIL);
        assertTrue(userExists);
    }


    @Test
    void testLoginUserSuccess() throws Exception{
        createUserInDb();
        UserLoginDto login = new UserLoginDto(TEST_USER, TEST_PASSWORD);


        var login_result = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value(TEST_USER))
                .andExpect(jsonPath("$.userId").exists())
                .andReturn();
        AuthResponseDto authDto = objectMapper.readValue(login_result.getResponse().getContentAsString(), AuthResponseDto.class);

        mockMvc.perform(get("/api/v1/transactions/user/balance")
                        .header("Authorization", "Bearer " + authDto.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.userId").exists());
    }

    private void createUserInDb() {
        User user = User.builder()
                .username(TEST_USER)
                .email(TEST_EMAIL)
                .password(securityConfig.passwordEncoder().encode(TEST_PASSWORD))
                .build();
        userRepository.save(user);
    }

    @Test
    void testAddTransaction() throws Exception{
        createUserInDb();
        UserLoginDto login = new UserLoginDto(TEST_USER, TEST_PASSWORD);


        var login_result = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value(TEST_USER))
                .andExpect(jsonPath("$.userId").exists())
                .andReturn();
        AuthResponseDto authDto = objectMapper.readValue(login_result.getResponse().getContentAsString(), AuthResponseDto.class);


        var dtoLists = TestDtoCreator.createRandomTransactionDtoLists(1, authDto.userId());
        var requestDto = dtoLists.createList().getFirst();
        var responseDto = dtoLists.responseList().getFirst();

        mockMvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + authDto.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(responseDto.amount()))
                .andExpect(jsonPath("$.category").value(responseDto.category()))
                .andExpect(jsonPath("$.description").value(responseDto.description()))
                .andExpect(jsonPath("$.userId").value(responseDto.userId()))
                .andExpect(jsonPath("$.type").value(responseDto.type().toString()));
    }
}
