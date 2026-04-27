package sdu.diploma.userservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoalStatsDto {
    private Long totalGoals;
    private Long activeGoals;
    private Long completedGoals;
    private Long failedGoals;
    private Double completionRate;
    private Double avgProgressPercentage;
}
