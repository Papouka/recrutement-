// src/main/java/com/beac/estage_backend/dto/DashboardStatsDto.java
package com.beac.estage_backend.dto;
import lombok.Data;
@Data
public class DashboardStatsDto {
    private long total;
    private long nouvelles;
    private long enAnalyse;
    private long preselectionnees;
}