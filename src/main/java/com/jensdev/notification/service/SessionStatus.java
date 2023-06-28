package com.jensdev.notification.service;


import com.jensdev.user.modal.User;
import jakarta.websocket.Session;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SessionStatus {
    Session session;
    User user;
    boolean waitingForResponse;
    public SessionStatus(Session session, User user) {
        this.session = session;
        this.user = user;
        this.waitingForResponse = false;
    }
}
