package sdu.diploma.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sdu.diploma.userservice.client.GoalServiceClient;
import sdu.diploma.userservice.dto.FriendProfileResponse;
import sdu.diploma.userservice.dto.GoalStatsDto;
import sdu.diploma.userservice.dto.TestStatsResponse;
import sdu.diploma.userservice.entity.UserProfile;
import sdu.diploma.userservice.enums.FriendshipStatus;
import sdu.diploma.userservice.enums.PrivacyType;
import sdu.diploma.userservice.exception.BusinessException;
import sdu.diploma.userservice.repository.FriendshipRepository;
import sdu.diploma.userservice.repository.UserProfileRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendProfileService {

    private final UserProfileRepository userProfileRepository;
    private final FriendshipRepository friendshipRepository;
    private final PsychologicalTestService testService;
    private final GoalServiceClient goalServiceClient;

    public FriendProfileResponse getFullProfile(Long targetUserId, Long requesterId) {
        UserProfile profile = userProfileRepository.findByUserId(targetUserId)
                .orElseThrow(() -> new BusinessException("Profile not found"));

        checkAccess(profile, targetUserId, requesterId);

        GoalStatsDto goalStats = fetchGoalStats(targetUserId);
        TestStatsResponse testStats = testService.getUserStats(targetUserId);

        int xp = calculateXp(goalStats, testStats);
        int level = calculateLevel(xp);

        return FriendProfileResponse.builder()
                .userId(profile.getUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .memberSince(profile.getCreatedAt())
                .level(level)
                .levelTitle(getLevelTitle(level))
                .xp(xp)
                .xpToNextLevel(calculateXpToNextLevel(xp, level))
                .totalGoals(goalStats.getTotalGoals())
                .activeGoals(goalStats.getActiveGoals())
                .completedGoals(goalStats.getCompletedGoals())
                .failedGoals(goalStats.getFailedGoals())
                .completionRate(goalStats.getCompletionRate())
                .avgProgress(goalStats.getAvgProgressPercentage())
                .avgMotivationLevel(testStats.getAvgMotivationLevel())
                .avgProductivityLevel(testStats.getAvgProductivityLevel())
                .avgStressLevel(testStats.getAvgStressLevel())
                .overallStatus(testStats.getOverallStatus())
                .totalTestsTaken(testStats.getTotalTestsTaken())
                .build();
    }

    private void checkAccess(UserProfile profile, Long targetUserId, Long requesterId) {
        if (targetUserId.equals(requesterId) || profile.getPrivacyType() == PrivacyType.PUBLIC) {
            return;
        }
        if (profile.getPrivacyType() == PrivacyType.FRIENDS_ONLY) {
            boolean areFriends = friendshipRepository.findBetweenUsers(requesterId, targetUserId)
                    .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                    .orElse(false);
            if (areFriends) return;
        }
        throw new BusinessException("This profile is private");
    }

    private GoalStatsDto fetchGoalStats(Long userId) {
        try {
            return goalServiceClient.getGoalStats(userId);
        } catch (Exception e) {
            log.warn("Could not fetch goal stats for userId={}: {}", userId, e.getMessage());
            GoalStatsDto empty = new GoalStatsDto();
            empty.setTotalGoals(0L);
            empty.setActiveGoals(0L);
            empty.setCompletedGoals(0L);
            empty.setFailedGoals(0L);
            empty.setCompletionRate(0.0);
            empty.setAvgProgressPercentage(0.0);
            return empty;
        }
    }

    private int calculateXp(GoalStatsDto goals, TestStatsResponse tests) {
        int xp = 0;
        xp += (goals.getCompletedGoals() != null ? goals.getCompletedGoals() : 0) * 150;
        xp += (int) ((goals.getCompletionRate() != null ? goals.getCompletionRate() : 0.0) * 100);
        xp += (tests.getTotalTestsTaken() != null ? tests.getTotalTestsTaken() : 0) * 30;
        xp += (int) ((tests.getAvgMotivationLevel() != null ? tests.getAvgMotivationLevel() : 0.0) * 5);
        xp += (int) ((tests.getAvgProductivityLevel() != null ? tests.getAvgProductivityLevel() : 0.0) * 5);
        return xp;
    }

    private int calculateLevel(int xp) {
        return (xp / 500) + 1;
    }

    private int calculateXpToNextLevel(int xp, int level) {
        return level * 500 - xp;
    }

    private String getLevelTitle(int level) {
        if (level <= 1)  return "Новичок";
        if (level <= 3)  return "Ученик";
        if (level <= 5)  return "Практик";
        if (level <= 8)  return "Мастер";
        if (level <= 11) return "Эксперт";
        return "Чемпион";
    }
}
