package com.eliteschool.common_utils.exception;

import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.common_utils.dto.CommonResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream().map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce("", (a, b) -> a + (!a.isEmpty() ? ", " : "") + b);

        return ResponseUtil.error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", errorMessage, "Invalid request data");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return ResponseUtil.error(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION", ex.getMessage(), "Data constraints violated");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleDuplicateKeyException(DuplicateKeyException ex) {
        log.warn("Duplicate key: {}", ex.getMessage());
        return ResponseUtil.error(HttpStatus.CONFLICT, "DUPLICATE_KEY", ex.getMessage(), "Duplicate entry");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());
        
        String message = ex.getMessage();
        if (message != null && (message.contains("unique constraint") || message.contains("Duplicate entry"))) {
            return ResponseUtil.error(HttpStatus.CONFLICT, "DUPLICATE_ENTRY", 
                    "A record with the same unique identifier already exists", "Duplicate entry detected");
        }
        
        return ResponseUtil.error(HttpStatus.BAD_REQUEST, "DATA_INTEGRITY_VIOLATION", 
                "Data integrity violated", "Invalid data provided");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseUtil.error(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", ex.getMessage(), "Requested resource not found");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseUtil.error(HttpStatus.FORBIDDEN, "ACCESS_DENIED", ex.getMessage(), "Access is denied");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not allowed: {}", ex.getMessage());
        return ResponseUtil.error(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", ex.getMessage(), "HTTP method not allowed");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseUtil.error(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", ex.getMessage(), "Invalid request parameters");
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleAppException(AppException ex) {
        log.warn("Application exception [{}]: {}", ex.getErrorCode(), ex.getMessage());
        String friendly = ex.getFriendlyMessage() != null && !ex.getFriendlyMessage().isBlank()
                ? ex.getFriendlyMessage()
                : ex.getMessage();
        return ResponseUtil.error(
                ex.getStatus(),
                ex.getErrorCode(),
                ex.getMessage(),
                friendly
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseDto<Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return ResponseUtil.internalError(ex);
    }
}
