package com.jensdev.notification.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class MessageMessageDto implements BaseMessageDto {
    @Override
    public NotificationType getType() {
        return NotificationType.MESSAGE;
    }
}
