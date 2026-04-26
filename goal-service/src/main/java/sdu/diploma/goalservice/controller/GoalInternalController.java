package sdu.diploma.goalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sdu.diploma.goalservice.dto.GoalStatsResponse;
import sdu.diploma.goalservice.service.GoalService;

@RestController
@RequestMapping("/goals/internal")
@RequiredArgsConstructor
@Tag(name = "Goals Internal", description = "Internal API for service-to-service communication")
public class GoalInternalController {

    private final GoalService goalService;

    @GetMapping("/{userId}/stats")
    @Operation(summary = "Get goal stats by userId (internal)")
    public ResponseEntity<GoalStatsResponse> getStatsInternal(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(goalService.getStats(userId));
    }
}
