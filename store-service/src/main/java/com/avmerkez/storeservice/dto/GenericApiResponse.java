package com.avmerkez.storeservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

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
} 