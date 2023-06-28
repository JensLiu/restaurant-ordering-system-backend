package com.jensdev.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HeartbeatMessage implements BaseMessageDto {
    @Override
    public NotificationType getType() {
        return NotificationType.HEARTBEAT;
    }

    @Override
    public String toJson() {
        return "{\"type\":\"" + getType() + "\"}";
    }
}
