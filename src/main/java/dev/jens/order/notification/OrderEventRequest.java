package dev.jens.order.notification;

import dev.jens.order.OrderStatus;

public class OrderEventRequest {
    private long senderId;
    private long orderId;
    OrderStatus prevStatus;
    OrderStatus newStatus;
}
