package sdu.diploma.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalAnalyticsResponse {
    private Long totalGoals;
    private Long activeGoals;
    private Long completedGoals;
    private Long failedGoals;
    private Double completionRate;
    private Double avgProgressPercentage;
    private ChartDataResponse statusChart;
}
