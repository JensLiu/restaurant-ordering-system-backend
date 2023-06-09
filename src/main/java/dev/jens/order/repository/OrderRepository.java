package dev.jens.order.repository;

import dev.jens.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByStripeIntentId(String stripeSessionId);
    List<Order> findAllByStripeSessionId(String stripeSessionId);

}
