// src/main/java/com/beac/estage_backend/dto/HrDashboardDto.java
package com.beac.estage_backend.dto;
import lombok.Data;
import java.util.List;
@Data
public class HrDashboardDto {
    private DashboardStatsDto stats;
    private List<RecentOfferDto> recentOffers;
}