package com.example.dto.jooq;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AuthorStatsDto {
    private String name;
    private Long bookCount;
    private Double avgPages;
    private LocalDate firstPublishDate;
    private LocalDate lastPublishDate;
}