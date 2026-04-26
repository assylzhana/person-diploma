package sdu.diploma.goalservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import sdu.diploma.goalservice.dto.GoalResponse;
import sdu.diploma.goalservice.dto.UpdateGoalRequest;
import sdu.diploma.goalservice.entity.Goal;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    GoalResponse toResponse(Goal goal);

    void updateFromRequest(UpdateGoalRequest request, @MappingTarget Goal goal);
}
