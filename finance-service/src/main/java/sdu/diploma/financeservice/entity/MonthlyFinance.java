package sdu.diploma.financeservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "monthly_finances",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "year", "month"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyFinance extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(name = "base_income", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal baseIncome = BigDecimal.ZERO;

    @OneToMany(mappedBy = "monthlyFinance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Income> incomes = new ArrayList<>();

    @OneToMany(mappedBy = "monthlyFinance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Expense> expenses = new ArrayList<>();
}
