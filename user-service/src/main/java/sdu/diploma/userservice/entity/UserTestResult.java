package sdu.diploma.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_test_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTestResult extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private PsychologicalTest test;

    @Column(name = "stress_level", nullable = false)
    private Integer stressLevel;

    @Column(name = "motivation_level", nullable = false)
    private Integer motivationLevel;

    @Column(name = "productivity_level", nullable = false)
    private Integer productivityLevel;

    @Column(columnDefinition = "TEXT")
    private String recommendations;
}
