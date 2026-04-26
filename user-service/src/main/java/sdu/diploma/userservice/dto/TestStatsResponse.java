package sdu.diploma.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestStatsResponse {
    private Double avgStressLevel;
    private Double avgMotivationLevel;
    private Double avgProductivityLevel;
    private Integer totalTestsTaken;
    private String overallStatus;
}
