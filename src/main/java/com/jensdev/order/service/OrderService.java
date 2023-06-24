package com.jensdev.order.service;

import com.jensdev.order.modal.OrderStatus;
import com.stripe.model.checkout.Session;
import com.jensdev.order.modal.Order;
import com.jensdev.order.dto.OrderRequestDto;
import com.jensdev.user.modal.User;

import java.util.List;

public interface OrderService {
    Session handleCheckoutInitiation(OrderRequestDto requestDto, User user);
    Session handleContinuedCheckoutInitiation(Long orderId, String successUrl, String cancelUrl);

    Order handleSuccessfulPayment(String stripeIntent, String stripeSessionId);

    List<Order> getOrdersForUser(Long userId);

    List<Order> getAllOrders();

    List<Order> getWaitingOrdersForToday();

    Order getOrderById(Long id);

    Order updateOrderStatus(Long id, OrderStatus newStatus);

}
