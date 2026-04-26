package sdu.diploma.analyticsservice.ai;

import java.util.List;

public interface AiRecommendationClient {
    List<String> getRecommendations(AiContext context);

    record AiContext(
            Long userId,
            Double completionRate,
            Long failedGoals,
            Long activeGoals,
            Double spentPercentage,
            boolean overBudget,
            Double stressLevel,
            Double productivityLevel
    ) {}
}
