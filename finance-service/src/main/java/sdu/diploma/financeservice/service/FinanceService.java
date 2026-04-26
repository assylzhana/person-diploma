package sdu.diploma.financeservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sdu.diploma.financeservice.dto.*;
import sdu.diploma.financeservice.entity.Expense;
import sdu.diploma.financeservice.entity.Income;
import sdu.diploma.financeservice.entity.MonthlyFinance;
import sdu.diploma.financeservice.enums.ExpenseCategory;
import sdu.diploma.financeservice.exception.BusinessException;
import sdu.diploma.financeservice.repository.ExpenseRepository;
import sdu.diploma.financeservice.repository.IncomeRepository;
import sdu.diploma.financeservice.repository.MonthlyFinanceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class FinanceService {

    private final MonthlyFinanceRepository monthlyFinanceRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    public MonthlyFinanceResponse createMonthlyFinance(Long userId, CreateMonthlyFinanceRequest request) {
        if (monthlyFinanceRepository.existsByUserIdAndYearAndMonth(userId, request.getYear(), request.getMonth())) {
            throw new BusinessException("Monthly finance plan already exists for " + request.getYear() + "/" + request.getMonth());
        }
        MonthlyFinance finance = MonthlyFinance.builder()
                .userId(userId)
                .year(request.getYear())
                .month(request.getMonth())
                .baseIncome(request.getBaseIncome())
                .build();
        return toResponse(monthlyFinanceRepository.save(finance));
    }

    public IncomeResponse addIncome(Long userId, Long financeId, AddIncomeRequest request) {
        MonthlyFinance finance = getFinanceForUser(userId, financeId);
        Income income = Income.builder()
                .monthlyFinance(finance)
                .amount(request.getAmount())
                .type(request.getType())
                .description(request.getDescription())
                .date(request.getDate())
                .build();
        income = incomeRepository.save(income);
        return IncomeResponse.builder()
                .id(income.getId())
                .amount(income.getAmount())
                .type(income.getType())
                .description(income.getDescription())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .build();
    }

    public ExpenseResponse addExpense(Long userId, Long financeId, AddExpenseRequest request) {
        MonthlyFinance finance = getFinanceForUser(userId, financeId);
        Expense expense = Expense.builder()
                .monthlyFinance(finance)
                .amount(request.getAmount())
                .category(request.getCategory())
                .description(request.getDescription())
                .date(request.getDate())
                .build();
        expense = expenseRepository.save(expense);
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .description(expense.getDescription())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public MonthlyFinanceResponse getMonthlyFinance(Long userId, Long financeId) {
        return toResponse(getFinanceForUser(userId, financeId));
    }

    @Transactional(readOnly = true)
    public MonthlyFinanceResponse getCurrentMonth(Long userId) {
        LocalDate now = LocalDate.now();
        MonthlyFinance finance = monthlyFinanceRepository
                .findByUserIdAndYearAndMonth(userId, now.getYear(), now.getMonthValue())
                .orElseThrow(() -> new BusinessException("No finance plan for current month. Please create one."));
        return toResponse(finance);
    }

    @Transactional(readOnly = true)
    public List<MonthlyFinanceResponse> getAllMonthlyFinances(Long userId) {
        return monthlyFinanceRepository.findAllByUserIdOrderByYearDescMonthDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FinanceStatsResponse getCurrentMonthStats(Long userId) {
        LocalDate now = LocalDate.now();
        MonthlyFinance finance = monthlyFinanceRepository
                .findByUserIdAndYearAndMonth(userId, now.getYear(), now.getMonthValue())
                .orElseThrow(() -> new BusinessException("No finance plan for current month"));

        BigDecimal additionalIncome = incomeRepository.sumByMonthlyFinanceId(finance.getId());
        BigDecimal totalIncome = finance.getBaseIncome().add(additionalIncome);
        BigDecimal totalExpenses = expenseRepository.sumByMonthlyFinanceId(finance.getId());
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        double spentPercentage = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? totalExpenses.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        return FinanceStatsResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balance(balance)
                .spentPercentage(Math.round(spentPercentage * 10.0) / 10.0)
                .overBudget(balance.compareTo(BigDecimal.ZERO) < 0)
                .build();
    }

    private MonthlyFinance getFinanceForUser(Long userId, Long financeId) {
        MonthlyFinance finance = monthlyFinanceRepository.findById(financeId)
                .orElseThrow(() -> new BusinessException("Finance plan not found: " + financeId));
        if (!finance.getUserId().equals(userId)) {
            throw new BusinessException("Access denied");
        }
        return finance;
    }

    private MonthlyFinanceResponse toResponse(MonthlyFinance finance) {
        BigDecimal additionalIncome = incomeRepository.sumByMonthlyFinanceId(finance.getId());
        BigDecimal totalIncome = finance.getBaseIncome().add(additionalIncome);
        BigDecimal totalExpenses = expenseRepository.sumByMonthlyFinanceId(finance.getId());
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        double spentPercentage = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? totalExpenses.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        Map<String, BigDecimal> expensesByCategory = new HashMap<>();
        for (Object[] row : expenseRepository.sumByCategory(finance.getId())) {
            expensesByCategory.put(((ExpenseCategory) row[0]).name(), (BigDecimal) row[1]);
        }

        List<IncomeResponse> incomes = finance.getIncomes().stream()
                .map(i -> IncomeResponse.builder()
                        .id(i.getId()).amount(i.getAmount()).type(i.getType())
                        .description(i.getDescription()).date(i.getDate()).createdAt(i.getCreatedAt()).build())
                .toList();

        List<ExpenseResponse> expenses = finance.getExpenses().stream()
                .map(e -> ExpenseResponse.builder()
                        .id(e.getId()).amount(e.getAmount()).category(e.getCategory())
                        .description(e.getDescription()).date(e.getDate()).createdAt(e.getCreatedAt()).build())
                .toList();

        return MonthlyFinanceResponse.builder()
                .id(finance.getId())
                .year(finance.getYear())
                .month(finance.getMonth())
                .baseIncome(finance.getBaseIncome())
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balance(balance)
                .spentPercentage(Math.round(spentPercentage * 10.0) / 10.0)
                .expensesByCategory(expensesByCategory)
                .incomes(incomes)
                .expenses(expenses)
                .build();
    }
}
