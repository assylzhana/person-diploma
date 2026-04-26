package sdu.diploma.analyticsservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "goal-service", url = "${GOAL_SERVICE_URL}")
public interface GoalClient {

    @GetMapping("/goals/internal/{userId}/stats")
    GoalStatsData getGoalStats(@PathVariable("userId") Long userId);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class GoalStatsData {
        private Long totalGoals;
        private Long activeGoals;
        private Long completedGoals;
        private Long failedGoals;
        private Double completionRate;
        private Double avgProgressPercentage;
    }
}
