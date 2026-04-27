package sdu.diploma.userservice.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sdu.diploma.userservice.dto.UpdateProfileRequest;
import sdu.diploma.userservice.dto.UserProfileResponse;
import sdu.diploma.userservice.entity.UserProfile;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-26T23:38:51+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class UserProfileMapperImpl implements UserProfileMapper {

    @Override
    public UserProfileResponse toResponse(UserProfile profile) {
        if ( profile == null ) {
            return null;
        }

        UserProfileResponse.UserProfileResponseBuilder userProfileResponse = UserProfileResponse.builder();

        userProfileResponse.id( profile.getId() );
        userProfileResponse.userId( profile.getUserId() );
        userProfileResponse.firstName( profile.getFirstName() );
        userProfileResponse.lastName( profile.getLastName() );
        userProfileResponse.email( profile.getEmail() );
        userProfileResponse.bio( profile.getBio() );
        userProfileResponse.avatarUrl( profile.getAvatarUrl() );
        userProfileResponse.privacyType( profile.getPrivacyType() );
        userProfileResponse.createdAt( profile.getCreatedAt() );

        return userProfileResponse.build();
    }

    @Override
    public void updateFromRequest(UpdateProfileRequest request, UserProfile profile) {
        if ( request == null ) {
            return;
        }

        if ( request.getFirstName() != null ) {
            profile.setFirstName( request.getFirstName() );
        }
        if ( request.getLastName() != null ) {
            profile.setLastName( request.getLastName() );
        }
        if ( request.getBio() != null ) {
            profile.setBio( request.getBio() );
        }
        if ( request.getAvatarUrl() != null ) {
            profile.setAvatarUrl( request.getAvatarUrl() );
        }
        if ( request.getPrivacyType() != null ) {
            profile.setPrivacyType( request.getPrivacyType() );
        }
    }
}
