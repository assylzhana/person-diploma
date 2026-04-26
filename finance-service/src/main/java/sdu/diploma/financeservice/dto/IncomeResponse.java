package sdu.diploma.financeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sdu.diploma.financeservice.enums.IncomeType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeResponse {
    private Long id;
    private BigDecimal amount;
    private IncomeType type;
    private String description;
    private LocalDate date;
    private LocalDateTime createdAt;
}
