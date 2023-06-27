package com.jensdev.notification.controller;

import com.jensdev.auth.service.JwtService;
import com.jensdev.common.exceptions.AuthException;
import com.jensdev.notification.dto.BaseNotificationDto;
import com.jensdev.notification.dto.OrderNotificationDto;
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
        User user = extractUser(token);
        log.info("User " + user.getEmail() + " connected to websocket");
        log.info("Session id: " + session.getId());
        NotificationService.addConnection(user, session);
    }

    @OnClose
    public void onClose(@PathParam("token") String token, Session session) {
        User user = extractUser(token);
        log.info("User " + user.getEmail( )+ " disconnected from websocket");
        NotificationService.removeConnection(user);
    }

    @OnMessage
    public void onMessage(String text) {
        // TODO: support for chat message and refactor
        OrderNotificationDto notification = (OrderNotificationDto) BaseNotificationDto.fromJson(text);
        log.info("recieved message " + notification);
        Order order = orderService.updateOrderStatus(notification.getOrderId(), notification.getOrderStatus());
        log.info("to notify: " + order.getUser());
        var dto = OrderNotificationDto.builder().orderId(order.getId()).orderStatus(order.getStatus()).build();
        NotificationService.notifyUser(order.getUser(), dto);

    }

    private User extractUser(String token) {
        try {
            String userEmail = jwtService.extractUsername(token);
            return userService.findUserByEmail(userEmail);
        } catch (ExpiredJwtException e) {
            String subject = e.getClaims().getSubject();
            System.out.println("claim subject " + subject);
            return userService.findUserByEmail(subject);
//            throw new AuthException("Token expired");
        }
    }
}
