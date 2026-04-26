package sdu.diploma.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import sdu.diploma.userservice.enums.PrivacyType;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String bio;
    private String avatarUrl;
    private PrivacyType privacyType;
}
