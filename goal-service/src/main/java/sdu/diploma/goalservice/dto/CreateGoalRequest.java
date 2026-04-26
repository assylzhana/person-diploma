package sdu.diploma.goalservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sdu.diploma.goalservice.enums.GoalCategory;
import sdu.diploma.goalservice.enums.GoalPeriodType;

import java.time.LocalDate;

@Data
public class CreateGoalRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Category is required")
    private GoalCategory category;

    @NotNull(message = "Period type is required")
    private GoalPeriodType periodType;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;
}
