package sdu.diploma.goalservice.enums;

import com.fasterxml.jackson.annotation.JsonAlias;

public enum GoalCategory {
    CAREER,
    EDUCATION,
    HEALTH,
    FINANCE,
    @JsonAlias("PERSONAL")
    PERSONAL_DEVELOPMENT
}
