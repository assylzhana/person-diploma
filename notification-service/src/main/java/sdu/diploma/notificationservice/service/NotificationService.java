package sdu.diploma.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sdu.diploma.notificationservice.dto.NotificationResponse;
import sdu.diploma.notificationservice.dto.SendNotificationRequest;
import sdu.diploma.notificationservice.entity.Notification;
import sdu.diploma.notificationservice.enums.NotificationStatus;
import sdu.diploma.notificationservice.enums.NotificationType;
import sdu.diploma.notificationservice.exception.BusinessException;
import sdu.diploma.notificationservice.mapper.NotificationMapper;
import sdu.diploma.notificationservice.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationResponse send(SendNotificationRequest request) {
        NotificationType type;
        try {
            type = NotificationType.valueOf(request.getType());
        } catch (IllegalArgumentException e) {
            type = NotificationType.SYSTEM;
        }

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(type)
                .status(NotificationStatus.UNREAD)
                .referenceId(request.getReferenceId())
                .build();

        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAll(Long userId) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(notificationMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnread(Long userId) {
        return notificationRepository.findAllByUserIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD)
                .stream().map(notificationMapper::toResponse).toList();
    }

    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException("Notification not found: " + notificationId));
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("Access denied");
        }
        notification.setStatus(NotificationStatus.READ);
        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    public void markAllAsRead(Long userId) {
        notificationRepository.findAllByUserIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD)
                .forEach(n -> {
                    n.setStatus(NotificationStatus.READ);
                    notificationRepository.save(n);
                });
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }
}
