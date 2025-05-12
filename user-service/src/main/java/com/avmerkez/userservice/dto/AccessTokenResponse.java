package com.avmerkez.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessTokenResponse {
    private String accessToken;
    private String tokenType;
}