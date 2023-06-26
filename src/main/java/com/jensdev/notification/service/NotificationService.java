package com.jensdev.notification.service;

import com.jensdev.notification.dto.BaseNotificationDto;
import com.jensdev.user.modal.User;
import jakarta.websocket.Session;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class NotificationService {

    public static void addConnection(User user, Session session) {
        switch (user.getRole()) {
            case CHEF -> UserConnectionContext.chefConnections.put(user, session);
            case CUSTOMER -> UserConnectionContext.customerConnections.put(user, session);
            case ADMIN -> UserConnectionContext.managerConnections.put(user, session);
        }
    }

    public static void removeConnection(User user) {
        switch (user.getRole()) {
            case CHEF -> UserConnectionContext.chefConnections.remove(user);
            case CUSTOMER -> UserConnectionContext.customerConnections.remove(user);
            case ADMIN -> UserConnectionContext.managerConnections.remove(user);
        }
    }

    public static <T extends BaseNotificationDto> void notifyAllChefs(T notification) {
        log.info("Sending notification to all chefs: " + notification.toString());
        UserConnectionContext.chefConnections.forEach((user, session) -> {
            if (session.isOpen()) {
                log.info("message to " + user.getEmail() + ": " + notification.toJson());
                session.getAsyncRemote().sendText(notification.toJson());
            }
        });
    }

    public static <T extends BaseNotificationDto> void notifyUser(User user, T notification) {
        log.info("notifying user " + user.getEmail() + ", " + notification.toString());
        Session session = null;
        switch (user.getRole()) {
            case CUSTOMER -> session = UserConnectionContext.customerConnections.get(user);
            case CHEF -> session = UserConnectionContext.chefConnections.get(user);
            case ADMIN -> session = UserConnectionContext.managerConnections.get(user);
        }
        if (session != null && session.isOpen()) {
            log.info("message to " + user.getEmail() + ": " + notification.toJson());
            session.getAsyncRemote().sendText(notification.toJson());
        }
    }


}
