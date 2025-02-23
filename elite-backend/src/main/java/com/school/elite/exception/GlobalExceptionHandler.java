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

    private ResponseEntity<CommonResponseDto> buildErrorResponse(HttpStatus status, String errorMessage) {
        return ResponseEntity.status(status)
                .body(CommonResponseDto.createCommonResponseDto(status.value(), null, errorMessage, null));
    }

    /*** 🔹 User Exceptions Handling ***/
    @ExceptionHandler(UserException.UserNotFoundException.class)
    public ResponseEntity<CommonResponseDto> handleUserNotFoundException(UserException.UserNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserException.InvalidUserException.class)
    public ResponseEntity<CommonResponseDto> handleInvalidUserException(UserException.InvalidUserException ex) {
        logger.error("Invalid user: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserException.InvalidUserCredentialException.class)
    public ResponseEntity<CommonResponseDto> handleInvalidUserCredentialException(UserException.InvalidUserCredentialException ex) {
        logger.error("Invalid user credentials: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UserException.MissingFieldException.class)
    public ResponseEntity<CommonResponseDto> handleMissingFieldException(UserException.MissingFieldException ex) {
        logger.error("Missing field: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /*** 🔹 Task Exceptions Handling ***/
    @ExceptionHandler(TaskException.ResourceNotFoundException.class)
    public ResponseEntity<CommonResponseDto> handleTaskResourceNotFoundException(TaskException.ResourceNotFoundException ex) {
        logger.error("Task not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TaskException.InvalidRequestException.class)
    public ResponseEntity<CommonResponseDto> handleTaskInvalidRequestException(TaskException.InvalidRequestException ex) {
        logger.error("Invalid task request: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /*** 🔹 Global Exception Handling ***/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseDto> handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
    }
}
