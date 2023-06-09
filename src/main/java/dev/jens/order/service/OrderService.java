package dev.jens.order.service;

import com.stripe.model.checkout.Session;
import dev.jens.order.Order;
import dev.jens.order.dto.OrderRequestDto;
import dev.jens.user.User;

import java.util.Optional;

public interface OrderService {
    Optional<Session> handleCheckoutInitiation(OrderRequestDto requestDto, User user);

    Order handleSuccessfulPayment(String stripeIntent, String stripeSessionId);

}
