package sdu.diploma.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sdu.diploma.userservice.entity.PsychologicalTest;

import java.util.List;

public interface PsychologicalTestRepository extends JpaRepository<PsychologicalTest, Long> {
    List<PsychologicalTest> findAllByActiveTrue();
}
