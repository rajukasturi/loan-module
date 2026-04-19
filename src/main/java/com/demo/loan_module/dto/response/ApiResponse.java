package com.demo.loan_module.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper.
 * Provides consistent response structure across all API endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private LocalDateTime timestamp;
    private boolean success;
    private String message;
    private String code;
    private T data;

    public ApiResponse(boolean success, String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(boolean success, String message, String code, T data) {
        this.timestamp = LocalDateTime.now();
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    /**
     * Factory method for success response.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", "200", data);
    }

    /**
     * Factory method for success response with custom message.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, "200", data);
    }

    /**
     * Factory method for error response.
     */
    public static <T> ApiResponse<T> error(String message, String code) {
        return new ApiResponse<>(false, message, code, null);
    }

    /**
     * Factory method for error response with default code.
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(message, "500");
    }
}
