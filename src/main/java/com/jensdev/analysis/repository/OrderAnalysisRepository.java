package com.jensdev.analysis.repository;

import com.jensdev.order.modal.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface OrderAnalysisRepository extends JpaRepository<Order, Long> {
    @Query("""
            SELECT new map(o.id as id, o.createdAt as creatTime, o.paidAt as paymentTime, o.servedAt as serveTime)
            FROM Order o
            """)
    List<Map<String, Object>> getOrderProcessTime();

    @Query("""
            SELECT new map(COUNT(o.id) as count, HOUR(o.paidAt) as hour)
            FROM Order o
            WHERE HOUR(o.paidAt) IS NOT NULL
            GROUP BY HOUR(o.paidAt)
            ORDER BY HOUR(o.paidAt)
            """)
    List<Map<String, Object>> getPeakHours();

    @Query("""
            SELECT new map(cat.id as id, cat.value as value, COUNT(*) as count)
            FROM OrderItem oi
            LEFT JOIN oi.menuItem.categories as cat
            GROUP BY cat.id, cat.value
            ORDER BY COUNT(*) DESC
            """)
    List<Map<String, Object>> getTrendingCategories();

    @Query("""
            SELECT new map(mi.id as id, mi.name as name, COUNT(*) as count)
            FROM MenuItem mi
            LEFT JOIN OrderItem oi ON mi.id = oi.menuItem.id
            GROUP BY mi
            ORDER BY COUNT(*) DESC
            """
    )
    List<Map<String, Object>> getTrendingMenuItems();

    @Query("""
            SELECT new map(u.id as id, u.firstname as firstname, u.lastname as lastname, u.imageSrc as imageSrc,
                        u.registeredAt as registeredAt, SUM(o.totalPrice) as totalSpend)
            FROM User u
            JOIN Order o ON o.user = u AND o.paidAt IS NOT NULL
            GROUP BY u
            HAVING u.role = com.jensdev.user.modal.Role.CUSTOMER
            ORDER BY SUM(o.totalPrice) DESC
            """)
    List<Map<String, Object>> getValuableCustomers();
}
