package com.eliteschool.common_utils.util;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ResponseUtil Tests")
class ResponseUtilTest {

    @Test
    @DisplayName("Should create success response with data")
    void shouldCreateSuccessResponseWithData() {
        // Arrange
        String message = "Operation successful";
        String data = "Test data";

        // Act
        ResponseEntity<CommonResponseDto<String>> response = ResponseUtil.success(message, data);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
        assertThat(response.getBody().getData()).isEqualTo(data);
        assertThat(response.getBody().getError()).isNull();
    }

    @Test
    @DisplayName("Should create success response without data")
    void shouldCreateSuccessResponseWithoutData() {
        // Arrange
        String message = "Operation completed";

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = ResponseUtil.success(message, null);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("Should create error response with all details")
    void shouldCreateErrorResponseWithAllDetails() {
        // Arrange
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorCode = "VALIDATION_ERROR";
        String message = "Validation failed";
        String errorDetails = "Email is required";

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = 
                ResponseUtil.error(status, errorCode, message, errorDetails);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getErrorCode()).isEqualTo(errorCode);
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("Should create error response without error details")
    void shouldCreateErrorResponseWithoutErrorDetails() {
        // Arrange
        HttpStatus status = HttpStatus.NOT_FOUND;
        String errorCode = "NOT_FOUND";
        String message = "Resource not found";

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = 
                ResponseUtil.error(status, errorCode, message, null);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getErrorCode()).isEqualTo(errorCode);
    }

    @Test
    @DisplayName("Should create unauthorized error response")
    void shouldCreateUnauthorizedErrorResponse() {
        // Arrange
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String errorCode = "UNAUTHORIZED";
        String message = "Invalid credentials";

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = 
                ResponseUtil.error(status, errorCode, message, null);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getError()).isNotNull();
    }

    @Test
    @DisplayName("Should create internal server error response")
    void shouldCreateInternalServerErrorResponse() {
        // Arrange
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorCode = "INTERNAL_ERROR";
        String message = "An unexpected error occurred";
        String errorDetails = "Database connection failed";

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = 
                ResponseUtil.error(status, errorCode, message, errorDetails);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getErrorCode()).isEqualTo(errorCode);
    }

    @Test
    @DisplayName("Should create success response with complex object")
    void shouldCreateSuccessResponseWithComplexObject() {
        // Arrange
        class TestData {
            String name = "Test";
            int value = 42;
        }
        TestData testData = new TestData();

        // Act
        ResponseEntity<CommonResponseDto<TestData>> response = 
                ResponseUtil.success("Data retrieved", testData);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().name).isEqualTo("Test");
        assertThat(response.getBody().getData().value).isEqualTo(42);
    }

    @Test
    @DisplayName("Should create conflict error response")
    void shouldCreateConflictErrorResponse() {
        // Arrange
        HttpStatus status = HttpStatus.CONFLICT;
        String errorCode = "RESOURCE_CONFLICT";
        String message = "Resource already exists";
        String errorDetails = "User with this email already exists";

        // Act
        ResponseEntity<CommonResponseDto<Object>> response = 
                ResponseUtil.error(status, errorCode, message, errorDetails);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getErrorCode()).isEqualTo(errorCode);
    }
}
