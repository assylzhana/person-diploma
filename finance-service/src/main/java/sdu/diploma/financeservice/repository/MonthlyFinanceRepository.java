package sdu.diploma.financeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sdu.diploma.financeservice.entity.MonthlyFinance;

import java.util.List;
import java.util.Optional;

public interface MonthlyFinanceRepository extends JpaRepository<MonthlyFinance, Long> {
    Optional<MonthlyFinance> findByUserIdAndYearAndMonth(Long userId, Integer year, Integer month);
    List<MonthlyFinance> findAllByUserIdOrderByYearDescMonthDesc(Long userId);
    boolean existsByUserIdAndYearAndMonth(Long userId, Integer year, Integer month);
}
