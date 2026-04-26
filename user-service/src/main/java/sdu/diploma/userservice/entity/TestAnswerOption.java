package sdu.diploma.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_answer_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestAnswerOption extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private TestQuestion question;

    @Column(nullable = false)
    private String optionText;

    @Column(name = "stress_score", nullable = false)
    private Integer stressScore;

    @Column(name = "motivation_score", nullable = false)
    private Integer motivationScore;

    @Column(name = "productivity_score", nullable = false)
    private Integer productivityScore;
}
