package sdu.diploma.goalservice.entity;

import jakarta.persistence.*;
import lombok.*;
import sdu.diploma.goalservice.enums.GoalCategory;
import sdu.diploma.goalservice.enums.GoalPeriodType;
import sdu.diploma.goalservice.enums.GoalStatus;

import java.time.LocalDate;

@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false)
    private GoalPeriodType periodType;

    @Column(name = "progress_percentage", nullable = false)
    @Builder.Default
    private Integer progressPercentage = 0;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(name = "deadline_notified")
    @Builder.Default
    private boolean deadlineNotified = false;
}
