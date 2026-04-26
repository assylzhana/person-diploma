package sdu.diploma.goalservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import sdu.diploma.goalservice.enums.GoalCategory;

import java.time.LocalDate;

@Data
public class UpdateGoalRequest {
    private String title;
    private String description;
    private GoalCategory category;
    private LocalDate deadline;

    @Min(0) @Max(100)
    private Integer progressPercentage;
}
