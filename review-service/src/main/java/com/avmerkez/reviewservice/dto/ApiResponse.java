package com.avmerkez.reviewservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API yanıt modeli")
public class ApiResponse<T> {
    
    @Schema(description = "İşlem başarılı mı?")
    private boolean success;
    
    @Schema(description = "Mesaj")
    private String message;
    
    @Schema(description = "Yanıt verisi")
    private T data;
    
    @Schema(description = "Yanıt zamanı")
    @Builder.Default
    private Instant timestamp = Instant.now();
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
    
    public void setData(T data) {
        this.data = data;
    }
} 