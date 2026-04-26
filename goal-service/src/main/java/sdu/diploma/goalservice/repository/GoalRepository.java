package sdu.diploma.goalservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sdu.diploma.goalservice.entity.Goal;
import sdu.diploma.goalservice.enums.GoalCategory;
import sdu.diploma.goalservice.enums.GoalStatus;

import java.time.LocalDate;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findAllByUserIdAndStatusNot(Long userId, GoalStatus status);

    List<Goal> findAllByUserIdAndCategory(Long userId, GoalCategory category);

    List<Goal> findAllByUserIdAndStatus(Long userId, GoalStatus status);

    @Query("SELECT g FROM Goal g WHERE g.userId = :userId AND g.status NOT IN ('DELETED') " +
           "AND (:category IS NULL OR g.category = :category) " +
           "AND (:status IS NULL OR g.status = :status)")
    List<Goal> findFiltered(@Param("userId") Long userId,
                            @Param("category") GoalCategory category,
                            @Param("status") GoalStatus status);

    @Query("SELECT g FROM Goal g WHERE g.status = 'ACTIVE' AND g.deadline < :date AND g.deadlineNotified = false")
    List<Goal> findOverdueGoals(@Param("date") LocalDate date);

    @Query("SELECT g FROM Goal g WHERE g.status = 'ACTIVE' AND g.deadline BETWEEN :today AND :tomorrow AND g.deadlineNotified = false")
    List<Goal> findGoalsWithDeadlineTomorrow(@Param("today") LocalDate today, @Param("tomorrow") LocalDate tomorrow);

    @Query("SELECT COUNT(g) FROM Goal g WHERE g.userId = :userId AND g.status != 'DELETED'")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(g) FROM Goal g WHERE g.userId = :userId AND g.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") GoalStatus status);

    @Query("SELECT AVG(g.progressPercentage) FROM Goal g WHERE g.userId = :userId AND g.status = 'ACTIVE'")
    Double avgProgressByUserId(@Param("userId") Long userId);
}
