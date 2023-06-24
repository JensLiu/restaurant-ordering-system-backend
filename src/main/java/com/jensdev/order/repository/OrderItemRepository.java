package com.jensdev.order.repository;

import com.jensdev.order.modal.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi JOIN oi.size WHERE oi.id IN :ids")
    List<OrderItem> findAllById(List<Long> ids);
}
