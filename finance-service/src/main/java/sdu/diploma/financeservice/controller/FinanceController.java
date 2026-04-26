package sdu.diploma.financeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sdu.diploma.financeservice.dto.*;
import sdu.diploma.financeservice.service.FinanceService;

import java.util.List;

@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
@Tag(name = "Finance", description = "Finance tracking APIs")
public class FinanceController {

    private final FinanceService financeService;

    @PostMapping("/monthly")
    @Operation(summary = "Create monthly finance plan")
    public ResponseEntity<MonthlyFinanceResponse> createMonthly(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateMonthlyFinanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeService.createMonthlyFinance(userId, request));
    }

    @GetMapping("/monthly")
    @Operation(summary = "Get all monthly finance plans")
    public ResponseEntity<List<MonthlyFinanceResponse>> getAllMonthly(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(financeService.getAllMonthlyFinances(userId));
    }

    @GetMapping("/monthly/current")
    @Operation(summary = "Get current month finance plan")
    public ResponseEntity<MonthlyFinanceResponse> getCurrentMonth(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(financeService.getCurrentMonth(userId));
    }

    @GetMapping("/monthly/{financeId}")
    @Operation(summary = "Get finance plan by ID")
    public ResponseEntity<MonthlyFinanceResponse> getMonthly(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("financeId") Long financeId) {
        return ResponseEntity.ok(financeService.getMonthlyFinance(userId, financeId));
    }

    @PostMapping("/monthly/{financeId}/income")
    @Operation(summary = "Add income to monthly plan")
    public ResponseEntity<IncomeResponse> addIncome(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("financeId") Long financeId,
            @Valid @RequestBody AddIncomeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeService.addIncome(userId, financeId, request));
    }

    @PostMapping("/monthly/{financeId}/expense")
    @Operation(summary = "Add expense to monthly plan")
    public ResponseEntity<ExpenseResponse> addExpense(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("financeId") Long financeId,
            @Valid @RequestBody AddExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeService.addExpense(userId, financeId, request));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get current month financial stats")
    public ResponseEntity<FinanceStatsResponse> getStats(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(financeService.getCurrentMonthStats(userId));
    }
}
