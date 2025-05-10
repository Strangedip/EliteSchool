package com.eliteschool.store_service.exception;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.util.ResponseUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for consistent error responses across the application.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation exceptions from request body validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (a, b) -> a + (!a.isEmpty() ? ", " : "") + b);
        
        return ResponseUtil.error(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                errorMessage,
                "Validation failed for request"
        );
    }

    /**
     * Handle constraint violations (e.g., unique constraints).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());
        return ResponseUtil.error(
                HttpStatus.BAD_REQUEST,
                "CONSTRAINT_VIOLATION",
                ex.getMessage(),
                "Data constraints violated"
        );
    }
    
    /**
     * Handle database integrity violations (e.g., duplicate keys, foreign key violations).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());
        
        String message = ex.getMessage();
        if (message.contains("unique constraint") || message.contains("Duplicate entry")) {
            return ResponseUtil.error(
                    HttpStatus.CONFLICT,
                    "DUPLICATE_ENTRY",
                    "A record with the same unique identifier already exists",
                    "Duplicate entry detected"
            );
        }
        
        return ResponseUtil.error(
                HttpStatus.BAD_REQUEST,
                "DATA_INTEGRITY_VIOLATION",
                ex.getMessage(),
                "Data integrity violated"
        );
    }
    
    /**
     * Handle entity not found exceptions.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("Entity not found: {}", ex.getMessage());
        return ResponseUtil.error(
                HttpStatus.NOT_FOUND,
                "ENTITY_NOT_FOUND",
                ex.getMessage(),
                "Requested entity not found"
        );
    }
    
    /**
     * Catch-all handler for any unhandled exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseDto<Void>> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return ResponseUtil.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                ex.getMessage(),
                "An unexpected error occurred"
        );
    }
} 