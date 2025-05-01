package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    @NotBlank
    private long id;
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
