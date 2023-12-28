package com.jensdev.notification.service;

import com.jensdev.notification.dto.BaseMessageDto;
import com.jensdev.user.modal.User;
import jakarta.websocket.Session;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.JstlUtils;

import java.util.List;

@Service
@Log4j2
public class NotificationService {

    public static void addConnection(User user, Session session) {
        switch (user.getRole()) {
            case CHEF -> UserConnectionContext.chefConnections.put(user.getId(), new SessionStatus(session, user));
            case CUSTOMER ->
                    UserConnectionContext.customerConnections.put(user.getId(), new SessionStatus(session, user));
            case ADMIN -> UserConnectionContext.managerConnections.put(user.getId(), new SessionStatus(session, user));
        }
    }

    public static void removeConnection(User user) {
        switch (user.getRole()) {
            case CHEF -> UserConnectionContext.chefConnections.remove(user.getId());
            case CUSTOMER -> UserConnectionContext.customerConnections.remove(user.getId());
            case ADMIN -> UserConnectionContext.managerConnections.remove(user.getId());
        }
    }

    public static void deleteBySession(Session session) {
        // this is not a good way of removing a connection
        // it should only be used when the exception only contains
        // the session and not the user
        UserConnectionContext.chefConnections.forEach((id, status) -> {
            if (status.getSession().equals(session)) {
                UserConnectionContext.chefConnections.remove(id);
            }
        });
        UserConnectionContext.customerConnections.forEach((id, status) -> {
            if (status.getSession().equals(session)) {
                UserConnectionContext.customerConnections.remove(id);
            }
        });
        UserConnectionContext.managerConnections.forEach((id, status) -> {
            if (status.getSession().equals(session)) {
                UserConnectionContext.managerConnections.remove(id);
            }
        });
    }

    public static <T extends BaseMessageDto> void notifyAllChefs(T notification) {
        log.info("Sending notification to all chefs: " + notification.toString());
        UserConnectionContext.chefConnections.forEach((id, status) -> {
            User user = status.getUser();
            Session session = status.getSession();
            if (session == null) {
                log.info("session is null");
                UserConnectionContext.chefConnections.remove(id);
            }
            if (session.isOpen()) {
                log.info("message to " + user.getEmail() + ": " + notification.toJson());
                session.getAsyncRemote().sendText(notification.toJson());
            }
        });
    }

    public static <T extends BaseMessageDto> void notifyAllChefsBut(T notification, List<User> excludedUser) {
        log.info("Sending notification to all chefs : " + notification.toString());
        UserConnectionContext.chefConnections.forEach((id, status) -> {
            User user = status.getUser();
            Session session = status.getSession();
            if (session == null) {
                log.info("session is null");
                UserConnectionContext.chefConnections.remove(id);
            }
            if (session.isOpen() && !excludedUser.contains(user)) {
                log.info("message to " + user.getEmail() + ": " + notification.toJson());
                session.getAsyncRemote().sendText(notification.toJson());
            }
        });
    }

    public static <T extends BaseMessageDto> void notifyUser(User user, T notification) {
        log.info("notifying user " + user.getEmail() + ", " + notification.toString());
        SessionStatus status = null;
        switch (user.getRole()) {
            case CUSTOMER -> status = UserConnectionContext.customerConnections.get(user.getId());
            case CHEF -> status = UserConnectionContext.chefConnections.get(user.getId());
            case ADMIN -> status = UserConnectionContext.managerConnections.get(user.getId());
        }
        if (status != null) {
            var session = status.getSession();
            if (session.isOpen()) {
                log.info("message to " + user.getEmail() + ": " + notification.toJson());
                session.getAsyncRemote().sendText(notification.toJson());
            }
        }
    }

}
