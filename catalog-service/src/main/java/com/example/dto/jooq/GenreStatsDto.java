package com.example.dto.jooq;

import lombok.Data;

@Data
public class GenreStatsDto {
    private String genre;
    private Long bookCount;
    private Double avgPages;
}