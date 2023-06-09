package dev.jens.order.repository;

import dev.jens.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi JOIN oi.size WHERE oi.id IN :ids")
    List<OrderItem> findAllById(List<Long> ids);
}
