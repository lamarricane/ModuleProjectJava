package com.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookResponse {
    private String title;
    private String genre;
    private int pagesNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishingDate;
    private String description;
    private AuthorInfo author;
    private LocalDateTime addedAt;

    @Data
    public static class AuthorInfo {
        private String name;
    }
}