package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReaderRequest {
    @NotBlank
    private String username;
    @NotNull
    private long bookId;
}