package com.jensdev.notification.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class MessageNotificationDto implements BaseNotificationDto {
    @Override
    public NotificationType getType() {
        return NotificationType.MESSAGE;
    }
}
