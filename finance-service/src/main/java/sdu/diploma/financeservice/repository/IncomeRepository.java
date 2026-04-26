package sdu.diploma.financeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sdu.diploma.financeservice.entity.Income;

import java.math.BigDecimal;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.monthlyFinance.id = :financeId")
    BigDecimal sumByMonthlyFinanceId(@Param("financeId") Long financeId);
}
