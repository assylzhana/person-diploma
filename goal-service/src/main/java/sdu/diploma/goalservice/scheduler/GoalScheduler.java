package sdu.diploma.goalservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sdu.diploma.goalservice.client.NotificationClient;
import sdu.diploma.goalservice.entity.Goal;
import sdu.diploma.goalservice.enums.GoalStatus;
import sdu.diploma.goalservice.repository.GoalRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoalScheduler {

    private final GoalRepository goalRepository;
    private final NotificationClient notificationClient;

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void checkDeadlines() {
        log.info("Running goal deadline check...");
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<Goal> tomorrowGoals = goalRepository.findGoalsWithDeadlineTomorrow(today, tomorrow);
        for (Goal goal : tomorrowGoals) {
            sendDeadlineNotification(goal);
            goal.setDeadlineNotified(true);
            goalRepository.save(goal);
        }

        List<Goal> overdueGoals = goalRepository.findOverdueGoals(today);
        for (Goal goal : overdueGoals) {
            goal.setStatus(GoalStatus.FAILED);
            goal.setDeadlineNotified(true);
            goalRepository.save(goal);
            sendExpiredNotification(goal);
        }

        log.info("Goal deadline check complete. Tomorrow: {}, Overdue: {}", tomorrowGoals.size(), overdueGoals.size());
    }

    private void sendDeadlineNotification(Goal goal) {
        try {
            notificationClient.sendNotification(
                    NotificationClient.SendNotificationRequest.builder()
                            .userId(goal.getUserId())
                            .title("Goal Deadline Tomorrow")
                            .message("Your goal '" + goal.getTitle() + "' is due tomorrow!")
                            .type("GOAL_DEADLINE")
                            .referenceId(goal.getId())
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to send deadline notification for goal {}: {}", goal.getId(), e.getMessage());
        }
    }

    private void sendExpiredNotification(Goal goal) {
        try {
            notificationClient.sendNotification(
                    NotificationClient.SendNotificationRequest.builder()
                            .userId(goal.getUserId())
                            .title("Goal Expired")
                            .message("Your goal '" + goal.getTitle() + "' has expired without completion.")
                            .type("GOAL_EXPIRED")
                            .referenceId(goal.getId())
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to send expired notification for goal {}: {}", goal.getId(), e.getMessage());
        }
    }
}
