package com.avmerkez.mallservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Null alanları JSON'a dahil etme
public class GenericApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    // Hata durumları için ayrı bir ErrorResponse nesnesi kullanılabilir veya buraya eklenebilir.
    // Şimdilik GlobalExceptionHandler kendi ErrorResponse formatını kullanmaya devam edecek.

    public static <T> GenericApiResponse<T> success(T data) {
        return GenericApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> GenericApiResponse<T> success(T data, String message) {
        return GenericApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    // Hata durumları için de static factory metotları eklenebilir
    // public static ApiResponse<Object> error(String message, ...) {}
} 