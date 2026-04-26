package sdu.diploma.notificationservice.mapper;

import org.mapstruct.Mapper;
import sdu.diploma.notificationservice.dto.NotificationResponse;
import sdu.diploma.notificationservice.entity.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification notification);
}
