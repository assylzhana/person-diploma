package sdu.diploma.goalservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import sdu.diploma.goalservice.dto.GoalResponse;
import sdu.diploma.goalservice.dto.UpdateGoalRequest;
import sdu.diploma.goalservice.entity.Goal;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    GoalResponse toResponse(Goal goal);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(UpdateGoalRequest request, @MappingTarget Goal goal);
}
