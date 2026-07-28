package ru.ivanov.financetracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.ivanov.financetracker.dto.ErrorResponseDto;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException exception){
        ErrorResponseDto errorDto = new ErrorResponseDto(
                ResourceNotFoundException.class.getSimpleName(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(AuthenticationException exception) {
        ErrorResponseDto errorDto = new ErrorResponseDto(
                ResourceNotFoundException.class.getSimpleName(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(RegistrationException exception) {
        ErrorResponseDto errorDto = new ErrorResponseDto(
                RegistrationException.class.getSimpleName(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception exception) {
        ErrorResponseDto errorDto = new ErrorResponseDto(
                ResourceNotFoundException.class.getSimpleName(),
                "Произошла внутренняя ошибка сервера.",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
