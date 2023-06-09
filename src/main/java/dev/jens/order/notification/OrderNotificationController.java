package dev.jens.order.notification;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class OrderNotificationController {
    @MessageMapping("/order_notification/order_ready")
    public OrderNotification orderReady(OrderEventRequest request) {
        return new OrderNotification();
    }
}
