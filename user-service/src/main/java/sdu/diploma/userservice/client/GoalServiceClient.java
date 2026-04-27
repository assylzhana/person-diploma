package sdu.diploma.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sdu.diploma.userservice.dto.GoalStatsDto;

@FeignClient(name = "goal-service", url = "${GOAL_SERVICE_URL}")
public interface GoalServiceClient {

    @GetMapping("/goals/internal/{userId}/stats")
    GoalStatsDto getGoalStats(@PathVariable("userId") Long userId);
}
