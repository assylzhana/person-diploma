package sdu.diploma.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sdu.diploma.userservice.enums.FriendshipStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipResponse {
    private Long id;
    private Long requesterId;
    private Long addresseeId;
    private FriendshipStatus status;
    private UserProfileResponse friend;
    private LocalDateTime createdAt;
}
