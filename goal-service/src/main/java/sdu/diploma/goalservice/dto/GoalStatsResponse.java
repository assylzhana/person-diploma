package sdu.diploma.goalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalStatsResponse {
    private Long totalGoals;
    private Long activeGoals;
    private Long completedGoals;
    private Long failedGoals;
    private Double completionRate;
    private Double avgProgressPercentage;
}
