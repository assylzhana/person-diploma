package sdu.diploma.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sdu.diploma.userservice.dto.FriendshipResponse;
import sdu.diploma.userservice.entity.Friendship;
import sdu.diploma.userservice.entity.UserProfile;
import sdu.diploma.userservice.enums.FriendshipStatus;
import sdu.diploma.userservice.exception.BusinessException;
import sdu.diploma.userservice.mapper.UserProfileMapper;
import sdu.diploma.userservice.repository.FriendshipRepository;
import sdu.diploma.userservice.repository.UserProfileRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public FriendshipResponse sendFriendRequest(Long requesterId, Long addresseeId) {
        if (requesterId.equals(addresseeId)) {
            throw new BusinessException("Cannot add yourself as a friend");
        }
        friendshipRepository.findBetweenUsers(requesterId, addresseeId).ifPresent(f -> {
            throw new BusinessException("Friendship already exists with status: " + f.getStatus());
        });

        Friendship friendship = Friendship.builder()
                .requesterId(requesterId)
                .addresseeId(addresseeId)
                .status(FriendshipStatus.PENDING)
                .build();
        return toResponse(friendshipRepository.save(friendship), requesterId);
    }

    public FriendshipResponse acceptFriendRequest(Long userId, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new BusinessException("Friend request not found"));
        if (!friendship.getAddresseeId().equals(userId)) {
            throw new BusinessException("Not authorized to accept this request");
        }
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return toResponse(friendshipRepository.save(friendship), userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        Friendship friendship = friendshipRepository.findBetweenUsers(userId, friendId)
                .orElseThrow(() -> new BusinessException("Friendship not found"));
        friendshipRepository.delete(friendship);
    }

    @Transactional(readOnly = true)
    public List<FriendshipResponse> getFriends(Long userId) {
        return friendshipRepository.findAllByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED)
                .stream()
                .map(f -> toResponse(f, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendshipResponse> getPendingRequests(Long userId) {
        return friendshipRepository.findAllByUserIdAndStatus(userId, FriendshipStatus.PENDING)
                .stream()
                .map(f -> toResponse(f, userId))
                .toList();
    }

    private FriendshipResponse toResponse(Friendship friendship, Long currentUserId) {
        Long friendId = friendship.getRequesterId().equals(currentUserId)
                ? friendship.getAddresseeId()
                : friendship.getRequesterId();

        UserProfile friendProfile = userProfileRepository.findByUserId(friendId).orElse(null);

        return FriendshipResponse.builder()
                .id(friendship.getId())
                .requesterId(friendship.getRequesterId())
                .addresseeId(friendship.getAddresseeId())
                .status(friendship.getStatus())
                .friend(friendProfile != null ? userProfileMapper.toResponse(friendProfile) : null)
                .createdAt(friendship.getCreatedAt())
                .build();
    }
}
