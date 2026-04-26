package sdu.diploma.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sdu.diploma.userservice.enums.PrivacyType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private String avatarUrl;
    private PrivacyType privacyType;
    private LocalDateTime createdAt;
}
