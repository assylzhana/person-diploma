package sdu.diploma.analyticsservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-client", url = "${USER_SERVICE_URL}")
public interface UserClient {

    @GetMapping("/users/tests/internal/{userId}/stats")
    TestStatsData getTestStats(@PathVariable("userId") Long userId);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class TestStatsData {
        private Double avgStressLevel;
        private Double avgMotivationLevel;
        private Double avgProductivityLevel;
        private Integer totalTestsTaken;
        private String overallStatus;
    }
}
