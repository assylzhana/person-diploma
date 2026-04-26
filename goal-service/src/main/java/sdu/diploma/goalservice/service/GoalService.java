package sdu.diploma.goalservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sdu.diploma.goalservice.dto.CreateGoalRequest;
import sdu.diploma.goalservice.dto.GoalResponse;
import sdu.diploma.goalservice.dto.GoalStatsResponse;
import sdu.diploma.goalservice.dto.UpdateGoalRequest;
import sdu.diploma.goalservice.entity.Goal;
import sdu.diploma.goalservice.enums.GoalCategory;
import sdu.diploma.goalservice.enums.GoalPeriodType;
import sdu.diploma.goalservice.enums.GoalStatus;
import sdu.diploma.goalservice.exception.BusinessException;
import sdu.diploma.goalservice.mapper.GoalMapper;
import sdu.diploma.goalservice.repository.GoalRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    public GoalResponse create(Long userId, CreateGoalRequest request) {
        LocalDate deadline = calculateDeadline(request.getPeriodType(), request.getDeadline());

        Goal goal = Goal.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .periodType(request.getPeriodType())
                .status(GoalStatus.ACTIVE)
                .progressPercentage(0)
                .deadline(deadline)
                .build();

        return goalMapper.toResponse(goalRepository.save(goal));
    }

    public GoalResponse update(Long userId, Long goalId, UpdateGoalRequest request) {
        Goal goal = getGoalForUser(userId, goalId);
        goalMapper.updateFromRequest(request, goal);
        return goalMapper.toResponse(goalRepository.save(goal));
    }

    public GoalResponse complete(Long userId, Long goalId) {
        Goal goal = getGoalForUser(userId, goalId);
        if (goal.getStatus() != GoalStatus.ACTIVE) {
            throw new BusinessException("Only active goals can be completed");
        }
        goal.setStatus(GoalStatus.COMPLETED);
        goal.setProgressPercentage(100);
        return goalMapper.toResponse(goalRepository.save(goal));
    }

    public void delete(Long userId, Long goalId) {
        Goal goal = getGoalForUser(userId, goalId);
        goal.setStatus(GoalStatus.DELETED);
        goalRepository.save(goal);
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getGoals(Long userId, GoalCategory category, GoalStatus status) {
        return goalRepository.findFiltered(userId, category, status)
                .stream()
                .map(goalMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalResponse getGoal(Long userId, Long goalId) {
        return goalMapper.toResponse(getGoalForUser(userId, goalId));
    }

    @Transactional(readOnly = true)
    public GoalStatsResponse getStats(Long userId) {
        Long total = goalRepository.countByUserId(userId);
        Long active = goalRepository.countByUserIdAndStatus(userId, GoalStatus.ACTIVE);
        Long completed = goalRepository.countByUserIdAndStatus(userId, GoalStatus.COMPLETED);
        Long failed = goalRepository.countByUserIdAndStatus(userId, GoalStatus.FAILED);
        Double avgProgress = goalRepository.avgProgressByUserId(userId);

        double completionRate = total > 0 ? (completed * 100.0 / total) : 0.0;

        return GoalStatsResponse.builder()
                .totalGoals(total)
                .activeGoals(active)
                .completedGoals(completed)
                .failedGoals(failed)
                .completionRate(Math.round(completionRate * 10.0) / 10.0)
                .avgProgressPercentage(avgProgress != null ? Math.round(avgProgress * 10.0) / 10.0 : 0.0)
                .build();
    }

    private Goal getGoalForUser(Long userId, Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new BusinessException("Goal not found: " + goalId));
        if (!goal.getUserId().equals(userId)) {
            throw new BusinessException("Access denied to goal: " + goalId);
        }
        return goal;
    }

    private LocalDate calculateDeadline(GoalPeriodType type, LocalDate requestedDeadline) {
        if (requestedDeadline != null) return requestedDeadline;
        LocalDate now = LocalDate.now();
        return switch (type) {
            case DAILY -> now.plusDays(1);
            case WEEKLY -> now.plusWeeks(1);
            case MONTHLY -> now.plusMonths(1);
            case YEARLY -> now.plusYears(1);
        };
    }
}
