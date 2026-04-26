package sdu.diploma.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductivityAnalyticsResponse {
    private Double avgStressLevel;
    private Double avgMotivationLevel;
    private Double avgProductivityLevel;
    private Integer totalTestsTaken;
    private String overallStatus;
    private Double developmentScore;
    private ChartDataResponse productivityChart;
}
