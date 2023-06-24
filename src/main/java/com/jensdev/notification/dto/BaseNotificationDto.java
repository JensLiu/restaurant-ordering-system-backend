package com.jensdev.notification.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderNotificationDto.class, name = "ORDER"),
        @JsonSubTypes.Type(value = MessageNotificationDto.class, name = "MESSAGE")
})
public interface BaseNotificationDto {
    NotificationType getType();
    static BaseNotificationDto fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, BaseNotificationDto.class);
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