package com.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO для вывода книг.
 */
@Data
public class BookDetailsResponse {
    private String title;
    private String genre;
    private int pagesNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishingDate;
    private String description;
    private AuthorInfo author;

    @Data
    public static class AuthorInfo {
        private String name;
    }
}