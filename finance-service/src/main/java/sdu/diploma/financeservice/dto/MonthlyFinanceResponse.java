package sdu.diploma.financeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyFinanceResponse {
    private Long id;
    private Integer year;
    private Integer month;
    private BigDecimal baseIncome;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal balance;
    private Double spentPercentage;
    private Map<String, BigDecimal> expensesByCategory;
    private List<IncomeResponse> incomes;
    private List<ExpenseResponse> expenses;
}
