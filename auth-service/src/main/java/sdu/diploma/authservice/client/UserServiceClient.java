package sdu.diploma.authservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${USER_SERVICE_URL}")
public interface UserServiceClient {

    @PostMapping("/users/internal/profile")
    void createProfile(@RequestBody CreateProfileRequest request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateProfileRequest {
        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
    }
}
