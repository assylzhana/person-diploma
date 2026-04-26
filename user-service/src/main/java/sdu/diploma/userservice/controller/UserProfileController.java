package sdu.diploma.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sdu.diploma.userservice.dto.CreateUserProfileRequest;
import sdu.diploma.userservice.dto.UpdateProfileRequest;
import sdu.diploma.userservice.dto.UserProfileResponse;
import sdu.diploma.userservice.service.UserProfileService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Profiles", description = "User profile management APIs")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/internal/profile")
    @Operation(summary = "Create profile (internal, called by auth-service)")
    public ResponseEntity<Void> createProfile(@Valid @RequestBody CreateUserProfileRequest request) {
        userProfileService.createProfile(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get all public users")
    public ResponseEntity<List<UserProfileResponse>> getAllProfiles() {
        return ResponseEntity.ok(userProfileService.getAllProfiles());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile by userId")
    public ResponseEntity<UserProfileResponse> getProfileByUserId(
            @RequestHeader("X-User-Id") Long requesterId,
            @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userProfileService.getProfileByUserId(userId, requesterId));
    }
}
