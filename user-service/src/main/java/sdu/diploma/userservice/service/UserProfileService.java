package sdu.diploma.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sdu.diploma.userservice.dto.CreateUserProfileRequest;
import sdu.diploma.userservice.dto.UpdateProfileRequest;
import sdu.diploma.userservice.dto.UserProfileResponse;
import sdu.diploma.userservice.entity.UserProfile;
import sdu.diploma.userservice.enums.FriendshipStatus;
import sdu.diploma.userservice.enums.PrivacyType;
import sdu.diploma.userservice.exception.BusinessException;
import sdu.diploma.userservice.mapper.UserProfileMapper;
import sdu.diploma.userservice.repository.FriendshipRepository;
import sdu.diploma.userservice.repository.UserProfileRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserProfileMapper userProfileMapper;

    public void createProfile(CreateUserProfileRequest request) {
        if (userProfileRepository.existsByUserId(request.getUserId())) {
            return;
        }
        UserProfile profile = UserProfile.builder()
                .userId(request.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .privacyType(PrivacyType.PUBLIC)
                .build();
        userProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .map(userProfileMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Profile not found for userId: " + userId));
    }

    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Profile not found"));
        userProfileMapper.updateFromRequest(request, profile);
        return userProfileMapper.toResponse(userProfileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllProfiles() {
        return userProfileRepository.findAll().stream()
                .filter(p -> p.getPrivacyType() == PrivacyType.PUBLIC)
                .map(userProfileMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfileByUserId(Long targetUserId, Long requesterId) {
        UserProfile profile = userProfileRepository.findByUserId(targetUserId)
                .orElseThrow(() -> new BusinessException("Profile not found for userId: " + targetUserId));

        if (profile.getPrivacyType() == PrivacyType.PUBLIC || targetUserId.equals(requesterId)) {
            return userProfileMapper.toResponse(profile);
        }

        if (profile.getPrivacyType() == PrivacyType.FRIENDS_ONLY) {
            boolean areFriends = friendshipRepository.findBetweenUsers(requesterId, targetUserId)
                    .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                    .orElse(false);
            if (areFriends) {
                return userProfileMapper.toResponse(profile);
            }
        }

        throw new BusinessException("This profile is private");
    }
}
