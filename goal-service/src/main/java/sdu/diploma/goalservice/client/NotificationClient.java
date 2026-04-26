package sdu.diploma.goalservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${NOTIFICATION_SERVICE_URL}")
public interface NotificationClient {

    @PostMapping("/notifications/internal/send")
    void sendNotification(@RequestBody SendNotificationRequest request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class SendNotificationRequest {
        private Long userId;
        private String title;
        private String message;
        private String type;
        private Long referenceId;
    }
}
