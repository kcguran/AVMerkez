package com.avmerkez.userservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEventDto {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private LocalDateTime registeredAt;
} 