package com.example.dto.jooq;

import lombok.Data;

@Data
public class TotalStatsDto {
    private Long totalBooks;
    private Integer totalPages;
    private Double avgPagesAll;
}