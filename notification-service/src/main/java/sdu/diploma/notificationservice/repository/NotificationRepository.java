package sdu.diploma.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sdu.diploma.notificationservice.entity.Notification;
import sdu.diploma.notificationservice.enums.NotificationStatus;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findAllByUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status);
    long countByUserIdAndStatus(Long userId, NotificationStatus status);
}
