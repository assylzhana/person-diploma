package sdu.diploma.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sdu.diploma.analyticsservice.ai.AiRecommendationClient;
import sdu.diploma.analyticsservice.client.FinanceClient;
import sdu.diploma.analyticsservice.client.GoalClient;
import sdu.diploma.analyticsservice.client.UserClient;
import sdu.diploma.analyticsservice.dto.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final GoalClient goalClient;
    private final FinanceClient financeClient;
    private final UserClient userClient;
    private final AiRecommendationClient aiRecommendationClient;

    public GoalAnalyticsResponse getGoalAnalytics(Long userId) {
        GoalClient.GoalStatsData stats = fetchGoalStats(userId);

        ChartDataResponse chart = ChartDataResponse.builder()
                .labels(List.of("Active", "Completed", "Failed"))
                .values(List.of(stats.getActiveGoals(), stats.getCompletedGoals(), stats.getFailedGoals()))
                .chartType("doughnut")
                .build();

        return GoalAnalyticsResponse.builder()
                .totalGoals(stats.getTotalGoals())
                .activeGoals(stats.getActiveGoals())
                .completedGoals(stats.getCompletedGoals())
                .failedGoals(stats.getFailedGoals())
                .completionRate(stats.getCompletionRate())
                .avgProgressPercentage(stats.getAvgProgressPercentage())
                .statusChart(chart)
                .build();
    }

    public FinanceAnalyticsResponse getFinanceAnalytics(Long userId) {
        FinanceClient.FinanceStatsData stats = fetchFinanceStats(userId);

        ChartDataResponse chart = ChartDataResponse.builder()
                .labels(List.of("Income", "Expenses", "Balance"))
                .values(List.of(
                        stats.getTotalIncome() != null ? stats.getTotalIncome().doubleValue() : 0,
                        stats.getTotalExpenses() != null ? stats.getTotalExpenses().doubleValue() : 0,
                        stats.getBalance() != null ? stats.getBalance().doubleValue() : 0))
                .chartType("bar")
                .build();

        return FinanceAnalyticsResponse.builder()
                .totalIncome(stats.getTotalIncome())
                .totalExpenses(stats.getTotalExpenses())
                .balance(stats.getBalance())
                .spentPercentage(stats.getSpentPercentage())
                .overBudget(stats.isOverBudget())
                .budgetChart(chart)
                .build();
    }

    public ProductivityAnalyticsResponse getProductivityAnalytics(Long userId) {
        UserClient.TestStatsData stats = fetchTestStats(userId);

        double devScore = calculateDevelopmentScore(
                stats.getAvgStressLevel(), stats.getAvgMotivationLevel(), stats.getAvgProductivityLevel());

        ChartDataResponse chart = ChartDataResponse.builder()
                .labels(List.of("Stress (inverted)", "Motivation", "Productivity"))
                .values(List.of(
                        stats.getAvgStressLevel() != null ? (100 - stats.getAvgStressLevel()) : 50,
                        stats.getAvgMotivationLevel() != null ? stats.getAvgMotivationLevel() : 50,
                        stats.getAvgProductivityLevel() != null ? stats.getAvgProductivityLevel() : 50))
                .chartType("radar")
                .build();

        return ProductivityAnalyticsResponse.builder()
                .avgStressLevel(stats.getAvgStressLevel())
                .avgMotivationLevel(stats.getAvgMotivationLevel())
                .avgProductivityLevel(stats.getAvgProductivityLevel())
                .totalTestsTaken(stats.getTotalTestsTaken())
                .overallStatus(stats.getOverallStatus())
                .developmentScore(devScore)
                .productivityChart(chart)
                .build();
    }

    public OverallAnalyticsResponse getOverallAnalytics(Long userId) {
        GoalAnalyticsResponse goals = getGoalAnalytics(userId);
        FinanceAnalyticsResponse finance = getFinanceAnalytics(userId);
        ProductivityAnalyticsResponse productivity = getProductivityAnalytics(userId);

        double overallScore = calculateOverallScore(goals, finance, productivity);
        String level = determineDevelopmentLevel(overallScore);

        AiRecommendationClient.AiContext context = new AiRecommendationClient.AiContext(
                userId,
                goals.getCompletionRate(),
                goals.getFailedGoals(),
                goals.getActiveGoals(),
                finance.getSpentPercentage(),
                finance.isOverBudget(),
                productivity.getAvgStressLevel(),
                productivity.getAvgProductivityLevel()
        );

        List<String> recs = aiRecommendationClient.getRecommendations(context);

        return OverallAnalyticsResponse.builder()
                .goals(goals)
                .finance(finance)
                .productivity(productivity)
                .overallDevelopmentScore(overallScore)
                .developmentLevel(level)
                .recommendations(RecommendationResponse.builder()
                        .recommendations(recs)
                        .source("rule-based")
                        .build())
                .build();
    }

    public RecommendationResponse getRecommendations(Long userId) {
        GoalClient.GoalStatsData goals = fetchGoalStats(userId);
        FinanceClient.FinanceStatsData finance = fetchFinanceStats(userId);
        UserClient.TestStatsData tests = fetchTestStats(userId);

        AiRecommendationClient.AiContext context = new AiRecommendationClient.AiContext(
                userId,
                goals.getCompletionRate(),
                goals.getFailedGoals(),
                goals.getActiveGoals(),
                finance.getSpentPercentage(),
                finance.isOverBudget(),
                tests.getAvgStressLevel(),
                tests.getAvgProductivityLevel()
        );

        return RecommendationResponse.builder()
                .recommendations(aiRecommendationClient.getRecommendations(context))
                .source("rule-based")
                .build();
    }

    private GoalClient.GoalStatsData fetchGoalStats(Long userId) {
        try {
            return goalClient.getGoalStats(userId);
        } catch (Exception e) {
            log.warn("Could not fetch goal stats for user {}: {}", userId, e.getMessage());
            return GoalClient.GoalStatsData.builder()
                    .totalGoals(0L).activeGoals(0L).completedGoals(0L).failedGoals(0L)
                    .completionRate(0.0).avgProgressPercentage(0.0).build();
        }
    }

    private FinanceClient.FinanceStatsData fetchFinanceStats(Long userId) {
        try {
            return financeClient.getFinanceStats(userId);
        } catch (Exception e) {
            log.warn("Could not fetch finance stats for user {}: {}", userId, e.getMessage());
            return FinanceClient.FinanceStatsData.builder()
                    .totalIncome(BigDecimal.ZERO).totalExpenses(BigDecimal.ZERO)
                    .balance(BigDecimal.ZERO).spentPercentage(0.0).overBudget(false).build();
        }
    }

    private UserClient.TestStatsData fetchTestStats(Long userId) {
        try {
            return userClient.getTestStats(userId);
        } catch (Exception e) {
            log.warn("Could not fetch test stats for user {}: {}", userId, e.getMessage());
            return UserClient.TestStatsData.builder()
                    .avgStressLevel(50.0).avgMotivationLevel(50.0).avgProductivityLevel(50.0)
                    .totalTestsTaken(0).overallStatus("NO_DATA").build();
        }
    }

    private double calculateDevelopmentScore(Double stress, Double motivation, Double productivity) {
        double s = stress != null ? stress : 50.0;
        double m = motivation != null ? motivation : 50.0;
        double p = productivity != null ? productivity : 50.0;
        double score = (100 - s) * 0.3 + m * 0.35 + p * 0.35;
        return Math.round(score * 10.0) / 10.0;
    }

    private double calculateOverallScore(GoalAnalyticsResponse goals, FinanceAnalyticsResponse finance, ProductivityAnalyticsResponse productivity) {
        double goalScore = goals.getCompletionRate() != null ? goals.getCompletionRate() : 50.0;
        double financeScore = finance.isOverBudget() ? 20.0 : (100.0 - (finance.getSpentPercentage() != null ? finance.getSpentPercentage() : 50.0));
        double prodScore = productivity.getDevelopmentScore() != null ? productivity.getDevelopmentScore() : 50.0;
        double score = goalScore * 0.4 + financeScore * 0.3 + prodScore * 0.3;
        return Math.round(score * 10.0) / 10.0;
    }

    private String determineDevelopmentLevel(double score) {
        if (score >= 80) return "ELITE";
        if (score >= 65) return "ADVANCED";
        if (score >= 50) return "INTERMEDIATE";
        if (score >= 35) return "BEGINNER";
        return "STARTER";
    }
}
