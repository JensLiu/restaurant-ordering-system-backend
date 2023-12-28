package com.jensdev.notification.controller;

import com.jensdev.auth.service.JwtService;
import com.jensdev.notification.dto.BaseMessageDto;
import com.jensdev.notification.dto.NotificationType;
import com.jensdev.notification.dto.OrderMessageDto;
import com.jensdev.notification.service.NotificationService;
import com.jensdev.order.modal.Order;
import com.jensdev.order.service.OrderService;
import com.jensdev.user.modal.User;
import com.jensdev.user.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.jensdev.notification.controller.NotificationWsError.*;

@ServerEndpoint("/ws/notifications/{token}")
@Component
@Log4j2
public class NotificationEndpoint {
    // everytime a new connection is made, a new instance of this class is created
    // these services cannot be injected using constructor because it needs no args constructor to be created
    private static JwtService jwtService;
    private static UserService userService;
    private static OrderService orderService;

    @Resource
    public void setUserService(UserService userService) {
        NotificationEndpoint.userService = userService;
    }

    @Resource
    public void setJwtService(JwtService jwtService) {
        NotificationEndpoint.jwtService = jwtService;
    }

    @Resource
    public void setOrderService(OrderService orderService) {
        NotificationEndpoint.orderService = orderService;
    }

    @OnOpen
    public void onOpen(@PathParam("token") String token, Session session) {
        User user = verifyUser(token);
        if (user == null) {
            log.info("authentication failed");
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(TOKEN_EXPIRED_CODE), TOKEN_EXPIRED_ERR_STRING));
            } catch (Exception e) {
                log.error("Error closing session: " + e.getMessage());
            }
        } else {
            log.info("User " + user.getEmail() + " connected to websocket");
            log.info("Session id: " + session.getId());
            NotificationService.addConnection(user, session);
        }
    }

    @OnClose
    public void onClose(@PathParam("token") String token, Session session) {
        User user = getUserFromToken(token);
        log.info("User " + user.getEmail() + " disconnected from websocket");
        NotificationService.removeConnection(user);
    }

    @OnMessage
    public void onMessage(String text) {

        BaseMessageDto notification = null;
        try {
            notification = BaseMessageDto.fromJson(text);
        } catch (Exception e) {
            log.error("Error parsing message: " + e.getMessage());
            return;
        }

        assert notification != null;

        if (notification.getType() == NotificationType.ORDER) {
            OrderMessageDto orderNotificationDto = (OrderMessageDto) notification;
            log.info("recieved message " + notification);
            Order order = orderService.updateOrderStatus(orderNotificationDto.getOrderId(), orderNotificationDto.getOrderStatus());
            log.info("to notify: " + order.getUser());
            var dto = OrderMessageDto.builder().orderId(order.getId()).orderStatus(order.getStatus()).build();
            NotificationService.notifyUser(order.getUser(), dto);
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        log.error("Error: " + e.getMessage());
        try {
            session.close();
            log.info("Session closed: " + session.getId());
        } catch (IOException ex) {
            log.error("Error closing session: " + ex.getMessage());
        } finally {
            NotificationService.deleteBySession(session);
            log.info("Session removed: " + session.getId());
        }
    }

    private User getUserFromToken(String token) {
        try {
            String userEmail = jwtService.extractUsername(token);
            return userService.findUserByEmail(userEmail);
        } catch (ExpiredJwtException e) {
            String subject = e.getClaims().getSubject();
            return userService.findUserByEmail(subject);
        }
    }

    public User verifyUser(String token) {
        try {
            String userEmail = jwtService.extractUsername(token);
            return userService.findUserByEmail(userEmail);
        } catch (ExpiredJwtException e) {
            return null;
        }
    }
}
