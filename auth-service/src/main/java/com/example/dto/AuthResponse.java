package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для токена
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
}