package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO для пользователя
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}