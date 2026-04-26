package sdu.diploma.goalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sdu.diploma.goalservice.dto.CreateGoalRequest;
import sdu.diploma.goalservice.dto.GoalResponse;
import sdu.diploma.goalservice.dto.GoalStatsResponse;
import sdu.diploma.goalservice.dto.UpdateGoalRequest;
import sdu.diploma.goalservice.enums.GoalCategory;
import sdu.diploma.goalservice.enums.GoalStatus;
import sdu.diploma.goalservice.service.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
@Tag(name = "Goals", description = "Goal management APIs")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @Operation(summary = "Create a new goal")
    public ResponseEntity<GoalResponse> create(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateGoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.create(userId, request));
    }

    @PutMapping("/{goalId}")
    @Operation(summary = "Update a goal")
    public ResponseEntity<GoalResponse> update(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("goalId") Long goalId,
            @Valid @RequestBody UpdateGoalRequest request) {
        return ResponseEntity.ok(goalService.update(userId, goalId, request));
    }

    @PatchMapping("/{goalId}/complete")
    @Operation(summary = "Mark goal as completed")
    public ResponseEntity<GoalResponse> complete(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("goalId") Long goalId) {
        return ResponseEntity.ok(goalService.complete(userId, goalId));
    }

    @DeleteMapping("/{goalId}")
    @Operation(summary = "Delete a goal")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("goalId") Long goalId) {
        goalService.delete(userId, goalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get goals with optional filters")
    public ResponseEntity<List<GoalResponse>> getGoals(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) GoalCategory category,
            @RequestParam(required = false) GoalStatus status) {
        return ResponseEntity.ok(goalService.getGoals(userId, category, status));
    }

    @GetMapping("/{goalId}")
    @Operation(summary = "Get a specific goal")
    public ResponseEntity<GoalResponse> getGoal(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("goalId") Long goalId) {
        return ResponseEntity.ok(goalService.getGoal(userId, goalId));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get goal statistics for user")
    public ResponseEntity<GoalStatsResponse> getStats(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(goalService.getStats(userId));
    }
}
