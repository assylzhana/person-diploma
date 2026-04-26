package sdu.diploma.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTestResultResponse {
    private Long id;
    private Long testId;
    private String testTitle;
    private Integer stressLevel;
    private Integer motivationLevel;
    private Integer productivityLevel;
    private List<String> recommendations;
    private LocalDateTime createdAt;
}
