package sdu.diploma.analyticsservice.ai;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MockAiRecommendationClient implements AiRecommendationClient {

    @Override
    public List<String> getRecommendations(AiContext context) {
        List<String> recommendations = new ArrayList<>();

        if (context.failedGoals() != null && context.failedGoals() > 3) {
            recommendations.add("You have many failed goals. Consider reducing your workload and setting smaller, more achievable goals.");
        }

        if (context.completionRate() != null && context.completionRate() < 30.0) {
            recommendations.add("Your goal completion rate is low. Try breaking large goals into smaller milestones.");
        }

        if (context.overBudget()) {
            recommendations.add("Warning: Your expenses exceed your income this month. Review your spending habits.");
        }

        if (context.spentPercentage() != null && context.spentPercentage() > 80.0) {
            recommendations.add("You've spent " + String.format("%.1f", context.spentPercentage()) + "% of your income. Try to save more.");
        }

        if (context.stressLevel() != null && context.stressLevel() > 70.0) {
            recommendations.add("Your stress level is high. Consider scheduling downtime and self-care activities.");
        }

        if (context.productivityLevel() != null && context.productivityLevel() < 40.0) {
            recommendations.add("Your productivity is below average. Short daily goals and habit tracking can help.");
        }

        if (context.activeGoals() != null && context.activeGoals() == 0) {
            recommendations.add("You have no active goals. Set a new goal to keep yourself motivated!");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("You are performing well! Keep up the momentum and stay consistent.");
        }

        return recommendations;
    }
}
