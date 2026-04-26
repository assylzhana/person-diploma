package sdu.diploma.userservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class TakeTestRequest {

    @NotNull
    private Long testId;

    @NotEmpty(message = "Answers cannot be empty")
    private Map<Long, Long> answers; // questionId -> answerOptionId
}
