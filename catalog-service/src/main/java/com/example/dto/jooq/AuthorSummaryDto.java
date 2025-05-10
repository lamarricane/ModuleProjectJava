package com.example.dto.jooq;

import lombok.Data;

@Data
public class AuthorSummaryDto {
    private String name;
    private Long bookCount;
    private Integer totalPages;
}
