package com.eliteschool.common_utils.exception;

import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.common_utils.dto.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream().map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst().orElse("Validation failed");

        return ResponseUtil.error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", errorMessage, "Invalid request data");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleDuplicateKeyException(DuplicateKeyException ex) {
        return ResponseUtil.error(HttpStatus.CONFLICT, "DUPLICATE_KEY", ex.getMessage(), "Duplicate entry");
    }

//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<CommonResponseDto<Object>> handleBadCredentialsException(BadCredentialsException ex) {
//        return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "AUTH_FAILED", ex.getMessage(), "Invalid credentials");
//    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseUtil.error(HttpStatus.FORBIDDEN, "ACCESS_DENIED", ex.getMessage(), "Access is denied");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ResponseUtil.error(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", ex.getMessage(), "HTTP method not allowed");
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleAppException(AppException ex) {
        return ResponseUtil.error(
                ex.getStatus(),
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getFriendlyMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseDto<Object>> handleGenericException(Exception ex) {
        return ResponseUtil.internalError(ex);
    }
}
