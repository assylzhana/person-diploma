package sdu.diploma.financeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sdu.diploma.financeservice.entity.Expense;
import sdu.diploma.financeservice.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.monthlyFinance.id = :financeId")
    BigDecimal sumByMonthlyFinanceId(@Param("financeId") Long financeId);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.monthlyFinance.id = :financeId GROUP BY e.category")
    List<Object[]> sumByCategory(@Param("financeId") Long financeId);
}
