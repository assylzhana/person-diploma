package sdu.diploma.analyticsservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "finance-service", url = "${FINANCE_SERVICE_URL}")
public interface FinanceClient {

    @GetMapping("/finance/internal/{userId}/stats")
    FinanceStatsData getFinanceStats(@PathVariable("userId") Long userId);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class FinanceStatsData {
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal balance;
        private Double spentPercentage;
        private boolean overBudget;
    }
}
