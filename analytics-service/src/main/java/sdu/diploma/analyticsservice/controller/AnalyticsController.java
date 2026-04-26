package sdu.diploma.analyticsservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sdu.diploma.analyticsservice.dto.*;
import sdu.diploma.analyticsservice.service.AnalyticsService;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and reporting APIs")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    @Operation(summary = "Get full analytics overview")
    public ResponseEntity<OverallAnalyticsResponse> getOverall(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(analyticsService.getOverallAnalytics(userId));
    }

    @GetMapping("/goals")
    @Operation(summary = "Get goal analytics")
    public ResponseEntity<GoalAnalyticsResponse> getGoalAnalytics(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(analyticsService.getGoalAnalytics(userId));
    }

    @GetMapping("/finance")
    @Operation(summary = "Get finance analytics")
    public ResponseEntity<FinanceAnalyticsResponse> getFinanceAnalytics(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(analyticsService.getFinanceAnalytics(userId));
    }

    @GetMapping("/productivity")
    @Operation(summary = "Get productivity analytics")
    public ResponseEntity<ProductivityAnalyticsResponse> getProductivityAnalytics(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(analyticsService.getProductivityAnalytics(userId));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Get personalized recommendations")
    public ResponseEntity<RecommendationResponse> getRecommendations(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(analyticsService.getRecommendations(userId));
    }
}
