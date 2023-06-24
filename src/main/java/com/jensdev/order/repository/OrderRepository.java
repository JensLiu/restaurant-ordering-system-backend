package com.jensdev.order.repository;

import com.jensdev.order.modal.Order;
import com.jensdev.order.modal.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByStripeIntentId(String stripeSessionId);
    List<Order> findAllByStripeSessionId(String stripeSessionId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findAllByUserId(Long userId);

    @Query("SELECT o FROM Order o WHERE o.status in :statuses ORDER BY o.paidAt")
    List<Order> findAllWithStatuses(List<OrderStatus> statuses);

}
