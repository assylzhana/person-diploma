package sdu.diploma.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverallAnalyticsResponse {
    private GoalAnalyticsResponse goals;
    private FinanceAnalyticsResponse finance;
    private ProductivityAnalyticsResponse productivity;
    private Double overallDevelopmentScore;
    private String developmentLevel;
    private RecommendationResponse recommendations;
}
