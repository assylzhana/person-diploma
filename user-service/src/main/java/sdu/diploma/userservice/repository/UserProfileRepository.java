package sdu.diploma.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sdu.diploma.userservice.entity.UserProfile;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    boolean existsByEmail(String email);
}
