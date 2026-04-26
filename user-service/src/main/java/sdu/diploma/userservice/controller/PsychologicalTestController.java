package sdu.diploma.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sdu.diploma.userservice.dto.PsychTestResponse;
import sdu.diploma.userservice.dto.TakeTestRequest;
import sdu.diploma.userservice.dto.TestStatsResponse;
import sdu.diploma.userservice.dto.UserTestResultResponse;
import sdu.diploma.userservice.service.PsychologicalTestService;

import java.util.List;

@RestController
@RequestMapping("/users/tests")
@RequiredArgsConstructor
@Tag(name = "Psychological Tests", description = "Psychological test management APIs")
public class PsychologicalTestController {

    private final PsychologicalTestService testService;

    @GetMapping
    @Operation(summary = "Get all active psychological tests")
    public ResponseEntity<List<PsychTestResponse>> getAllTests() {
        return ResponseEntity.ok(testService.getAllActiveTests());
    }

    @GetMapping("/{testId}")
    @Operation(summary = "Get test by ID")
    public ResponseEntity<PsychTestResponse> getTest(@PathVariable("testId") Long testId) {
        return ResponseEntity.ok(testService.getTest(testId));
    }

    @PostMapping("/take")
    @Operation(summary = "Take a psychological test")
    public ResponseEntity<UserTestResultResponse> takeTest(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody TakeTestRequest request) {
        return ResponseEntity.ok(testService.takeTest(userId, request));
    }

    @GetMapping("/results")
    @Operation(summary = "Get my test results")
    public ResponseEntity<List<UserTestResultResponse>> getMyResults(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(testService.getUserResults(userId));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get my psychological stats")
    public ResponseEntity<TestStatsResponse> getMyStats(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(testService.getUserStats(userId));
    }
}
