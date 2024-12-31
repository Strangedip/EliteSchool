package com.school.elite.exception;

import com.school.elite.DTO.CommonResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserExceptions.UserNotFoundException.class)
    public ResponseEntity<CommonResponseDto> handleUserNotFoundException(UserExceptions.UserNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(createCommonResponseForException(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserExceptions.InvalidUserException.class)
    public ResponseEntity<CommonResponseDto> handleInvalidUserException(UserExceptions.InvalidUserException ex) {
        logger.error("Invalid user: {}", ex.getMessage());
        return new ResponseEntity<>(createCommonResponseForException(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserExceptions.InvalidUserCredentialException.class)
    public ResponseEntity<CommonResponseDto> handleInvalidUserCredentialException(UserExceptions.InvalidUserCredentialException ex) {
        logger.error("Invalid user credentials: {}", ex.getMessage());
        return new ResponseEntity<>(createCommonResponseForException(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserExceptions.MissingFieldException.class)
    public ResponseEntity<CommonResponseDto> handleMissingFieldException(UserExceptions.MissingFieldException ex) {
        logger.error("Missing field: {}", ex.getMessage());
        return new ResponseEntity<>(createCommonResponseForException(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseDto> handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createCommonResponseForException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private CommonResponseDto createCommonResponseForException(Integer statusCode, String errorMessage) {
        return CommonResponseDto.createCommonResponseDto(statusCode, null, errorMessage, null);
    }
}
