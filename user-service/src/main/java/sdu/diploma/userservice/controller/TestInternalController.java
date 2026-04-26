package sdu.diploma.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sdu.diploma.userservice.dto.TestStatsResponse;
import sdu.diploma.userservice.service.PsychologicalTestService;

@RestController
@RequestMapping("/users/tests/internal")
@RequiredArgsConstructor
@Tag(name = "Tests Internal", description = "Internal API for test stats")
public class TestInternalController {

    private final PsychologicalTestService testService;

    @GetMapping("/{userId}/stats")
    @Operation(summary = "Get test stats by userId (internal)")
    public ResponseEntity<TestStatsResponse> getStatsInternal(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(testService.getUserStats(userId));
    }
}
