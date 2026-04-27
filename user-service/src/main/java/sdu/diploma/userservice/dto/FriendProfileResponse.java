package sdu.diploma.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendProfileResponse {

    // Основная инфа
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private String avatarUrl;
    private LocalDateTime memberSince;

    // Уровень
    private Integer level;
    private String levelTitle;
    private Integer xp;
    private Integer xpToNextLevel;

    // Цели
    private Long totalGoals;
    private Long activeGoals;
    private Long completedGoals;
    private Long failedGoals;
    private Double completionRate;
    private Double avgProgress;

    // Психологический статус
    private Double avgMotivationLevel;
    private Double avgProductivityLevel;
    private Double avgStressLevel;
    private String overallStatus;
    private Integer totalTestsTaken;
}
