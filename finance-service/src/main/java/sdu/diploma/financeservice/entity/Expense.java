package sdu.diploma.financeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import sdu.diploma.financeservice.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_finance_id", nullable = false)
    private MonthlyFinance monthlyFinance;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Column
    private String description;

    @Column(nullable = false)
    private LocalDate date;
}
