package sdu.diploma.financeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sdu.diploma.financeservice.dto.FinanceStatsResponse;
import sdu.diploma.financeservice.service.FinanceService;

@RestController
@RequestMapping("/finance/internal")
@RequiredArgsConstructor
@Tag(name = "Finance Internal", description = "Internal API for service-to-service communication")
public class FinanceInternalController {

    private final FinanceService financeService;

    @GetMapping("/{userId}/stats")
    @Operation(summary = "Get finance stats by userId (internal)")
    public ResponseEntity<FinanceStatsResponse> getStatsInternal(@PathVariable("userId") Long userId) {
        try {
            return ResponseEntity.ok(financeService.getCurrentMonthStats(userId));
        } catch (Exception e) {
            return ResponseEntity.ok(FinanceStatsResponse.builder()
                    .totalIncome(java.math.BigDecimal.ZERO)
                    .totalExpenses(java.math.BigDecimal.ZERO)
                    .balance(java.math.BigDecimal.ZERO)
                    .spentPercentage(0.0)
                    .overBudget(false)
                    .build());
        }
    }
}
