package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO для книг.
 */
@Data
public class BookRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String genre;
    @Positive
    private int pagesNumber;
    @PastOrPresent
    private LocalDate publishingDate;
    private String description;
    @NotNull
    private long authorId;
}
