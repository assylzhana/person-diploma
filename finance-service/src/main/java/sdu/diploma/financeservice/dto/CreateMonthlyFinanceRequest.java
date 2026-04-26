package sdu.diploma.financeservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateMonthlyFinanceRequest {

    @NotNull
    @Min(2020)
    private Integer year;

    @NotNull
    @Min(1) @Max(12)
    private Integer month;

    @NotNull
    @Positive(message = "Base income must be positive")
    private BigDecimal baseIncome;
}
