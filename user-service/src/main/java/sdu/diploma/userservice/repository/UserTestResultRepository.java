package sdu.diploma.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sdu.diploma.userservice.entity.UserTestResult;

import java.util.List;

public interface UserTestResultRepository extends JpaRepository<UserTestResult, Long> {
    List<UserTestResult> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT AVG(r.stressLevel) FROM UserTestResult r WHERE r.userId = :userId")
    Double avgStressLevel(@Param("userId") Long userId);

    @Query("SELECT AVG(r.motivationLevel) FROM UserTestResult r WHERE r.userId = :userId")
    Double avgMotivationLevel(@Param("userId") Long userId);

    @Query("SELECT AVG(r.productivityLevel) FROM UserTestResult r WHERE r.userId = :userId")
    Double avgProductivityLevel(@Param("userId") Long userId);

    long countByUserId(Long userId);
}
