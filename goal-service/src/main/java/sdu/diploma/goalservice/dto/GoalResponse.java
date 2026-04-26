package sdu.diploma.goalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sdu.diploma.goalservice.enums.GoalCategory;
import sdu.diploma.goalservice.enums.GoalPeriodType;
import sdu.diploma.goalservice.enums.GoalStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private GoalCategory category;
    private GoalStatus status;
    private GoalPeriodType periodType;
    private Integer progressPercentage;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
