package com.avmerkez.storeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response structure")
public class ErrorResponse {
    @Schema(description = "Timestamp of when the error occurred", example = "2023-10-27T10:15:30")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "HTTP status error phrase", example = "Not Found")
    private String error;

    @Schema(description = "Detailed error message", example = "Store not found with id: 999")
    private String message;

    @Schema(description = "Request path where the error occurred", example = "/api/v1/stores/999")
    private String path;

    @Schema(description = "List of specific validation error details (if applicable)", nullable = true)
    private List<String> details; // For validation errors

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
} 