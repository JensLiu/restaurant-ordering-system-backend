package com.jensdev.notification.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderMessageDto.class, name = "ORDER"),
        @JsonSubTypes.Type(value = MessageMessageDto.class, name = "MESSAGE"),
        @JsonSubTypes.Type(value = HeartbeatMessage.class, name = "HEARTBEAT")
})
public interface BaseMessageDto {
    NotificationType getType();
    static BaseMessageDto fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, BaseMessageDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    default String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}