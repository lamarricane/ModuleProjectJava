package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO для авторов.
 */
@Data
public class AuthorRequest {
    @NotBlank
    private String name;
    @Past
    private LocalDate birthDate;
    @NotBlank
    private String location;
    private String bio;
}