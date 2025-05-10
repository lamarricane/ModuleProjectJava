package com.example.dto.jooq;

import lombok.Data;

import java.util.List;

@Data
public class CombinedStatsDto {
    private List<AuthorSummaryDto> authors;
    private TotalStatsDto totals;
}