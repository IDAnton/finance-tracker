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
    private TestDtoCreator dtoCreator;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private ObjectMapper objectMapper;

    String testUsername = "test_user";
    String testEmail = "test@mail.com";
    String testPass = "qwerty123";


    @Test
    void registerUser_Success() throws Exception{
        var userRegisterDto = UserRegisterDto.builder()
                .username(testUsername).email(testEmail).password(testPass).build();

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value(testUsername))
                .andExpect(jsonPath("$.email").value(testEmail));
        boolean userExists = userRepository.existsByEmail(testEmail);
        assertTrue(userExists);
    }


    @Test
    void loginUser_Success() throws Exception{
        createUserInDb();
        UserLoginDto login = new UserLoginDto(testUsername, testPass);


        var login_result = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value(testUsername))
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
                .username(testUsername)
                .email(testEmail)
                .password(securityConfig.passwordEncoder().encode(testPass))
                .build();
        userRepository.save(user);
    }

    @Test
    void addTransaction() throws Exception{
        createUserInDb();
        UserLoginDto login = new UserLoginDto(testUsername, testPass);


        var login_result = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value(testUsername))
                .andExpect(jsonPath("$.userId").exists())
                .andReturn();
        AuthResponseDto authDto = objectMapper.readValue(login_result.getResponse().getContentAsString(), AuthResponseDto.class);


        var dtoLists = dtoCreator.createRandomTransactionDtoLists(1, authDto.userId());
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
