package com.eliteschool.common_utils.exception;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle AppException correctly")
    void shouldHandleAppExceptionCorrectly() {
        // Arrange
        AppException exception = new AppException(
                "Test error message",
                "User-friendly message",
                "TEST_ERROR",
                HttpStatus.BAD_REQUEST
        );

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = exceptionHandler.handleAppException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Test error message");
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getErrorCode()).isEqualTo("TEST_ERROR");
    }

    @Test
    @DisplayName("Should handle EntityNotFoundException correctly")
    void shouldHandleEntityNotFoundExceptionCorrectly() {
        // Arrange
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = 
                exceptionHandler.handleEntityNotFound(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("Entity not found");
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException correctly")
    void shouldHandleIllegalArgumentExceptionCorrectly() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = 
                exceptionHandler.handleIllegalArgument(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("Invalid argument");
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException correctly")
    void shouldHandleDataIntegrityViolationExceptionCorrectly() {
        // Arrange
        DataIntegrityViolationException exception = 
                new DataIntegrityViolationException("Data integrity violation");

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = 
                exceptionHandler.handleDataIntegrityViolation(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("Data integrity");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException correctly")
    void shouldHandleMethodArgumentNotValidExceptionCorrectly() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("user", "email", "must not be blank");
        FieldError fieldError2 = new FieldError("user", "password", "must be at least 8 characters");
        
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = 
                exceptionHandler.handleValidationException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("email");
    }

    @Test
    @DisplayName("Should handle generic Exception correctly")
    void shouldHandleGenericExceptionCorrectly() {
        // Arrange
        Exception exception = new Exception("Generic error occurred");

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = 
                exceptionHandler.handleGenericException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
    }

    @Test
    @DisplayName("Should handle RuntimeException correctly")
    void shouldHandleRuntimeExceptionCorrectly() {
        // Arrange
        RuntimeException exception = new RuntimeException("Runtime error");

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = 
                exceptionHandler.handleGenericException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
    }
}
