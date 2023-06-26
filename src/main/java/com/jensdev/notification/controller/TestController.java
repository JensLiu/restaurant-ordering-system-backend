package com.jensdev.notification.controller;

import com.jensdev.notification.dto.MessageNotificationDto;
import com.jensdev.notification.dto.OrderNotificationDto;
import com.jensdev.notification.service.NotificationService;
import com.jensdev.order.modal.OrderStatus;
import com.jensdev.user.modal.User;
import com.jensdev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ws")
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;

    @GetMapping("/mock-orders")
    public void mockOrders() {
        System.out.println("Test begin");
        NotificationService.notifyAllChefs(OrderNotificationDto.builder().orderId(-1L).orderStatus(OrderStatus.WAITING).build());
        System.out.println("Test end");
    }

    @GetMapping("/mock-messages/{email}")
    public void mockMessages(@PathVariable String email) {
        System.out.println("Test begin");
        User user = userService.findUserByEmail(email);
        NotificationService.notifyUser(user, MessageNotificationDto.builder().build());
        System.out.println("Test end");
    }

}
