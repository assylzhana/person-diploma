package sdu.diploma.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sdu.diploma.userservice.dto.FriendshipResponse;
import sdu.diploma.userservice.service.FriendshipService;

import java.util.List;

@RestController
@RequestMapping("/users/friends")
@RequiredArgsConstructor
@Tag(name = "Friendships", description = "Friendship management APIs")
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping("/request/{addresseeId}")
    @Operation(summary = "Send friend request")
    public ResponseEntity<FriendshipResponse> sendRequest(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("addresseeId") Long addresseeId) {
        return ResponseEntity.ok(friendshipService.sendFriendRequest(userId, addresseeId));
    }

    @PutMapping("/{friendshipId}/accept")
    @Operation(summary = "Accept friend request")
    public ResponseEntity<FriendshipResponse> accept(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("friendshipId") Long friendshipId) {
        return ResponseEntity.ok(friendshipService.acceptFriendRequest(userId, friendshipId));
    }

    @DeleteMapping("/{friendId}")
    @Operation(summary = "Remove friend")
    public ResponseEntity<Void> remove(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("friendId") Long friendId) {
        friendshipService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get list of friends")
    public ResponseEntity<List<FriendshipResponse>> getFriends(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(friendshipService.getFriends(userId));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending friend requests")
    public ResponseEntity<List<FriendshipResponse>> getPending(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(friendshipService.getPendingRequests(userId));
    }
}
