package sdu.diploma.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import sdu.diploma.userservice.dto.UpdateProfileRequest;
import sdu.diploma.userservice.dto.UserProfileResponse;
import sdu.diploma.userservice.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileResponse toResponse(UserProfile profile);

    void updateFromRequest(UpdateProfileRequest request, @MappingTarget UserProfile profile);
}
