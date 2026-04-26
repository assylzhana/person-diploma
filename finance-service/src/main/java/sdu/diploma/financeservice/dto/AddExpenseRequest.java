package sdu.diploma.financeservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import sdu.diploma.financeservice.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AddExpenseRequest {

    @NotNull
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull
    private ExpenseCategory category;

    private String description;

    @NotNull
    private LocalDate date;
}
