package ru.ivanov.financetracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.financetracker.security.JwtTokenProvider;
import ru.ivanov.financetracker.dto.AuthResponseDto;
import ru.ivanov.financetracker.dto.UserLoginDto;
import ru.ivanov.financetracker.dto.UserRegisterDto;
import ru.ivanov.financetracker.dto.UserResponseDto;
import ru.ivanov.financetracker.exception.BadRequestException;
import ru.ivanov.financetracker.model.User;
import ru.ivanov.financetracker.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserResponseDto registerUser(UserRegisterDto dto){
        if (userRepository.existsByEmail(dto.getEmail())){
            throw new BadRequestException("такой email уже занят");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User newUser = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(encodedPassword)
                .build();

        var savedUser = userRepository.save(newUser);
        return mapToResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto loginUser(UserLoginDto dto){
        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new BadRequestException("Неверное имя пользователя или пароль"));

        if(!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadRequestException("Неверное имя пользователя или пароль");
        }

        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername());

        return new AuthResponseDto(token, user.getUsername(), user.getId());
    }


    private UserResponseDto mapToResponseDto(User entity) {
        return UserResponseDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
