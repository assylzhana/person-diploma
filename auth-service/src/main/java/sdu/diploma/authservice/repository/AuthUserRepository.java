package sdu.diploma.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sdu.diploma.authservice.entity.AuthUser;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
