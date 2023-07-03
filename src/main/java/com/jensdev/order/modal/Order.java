package com.jensdev.order.modal;

import com.jensdev.user.modal.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue
    Long id;

    String pickupCode;

    @ManyToOne
    User user;

    @Enumerated
    OrderStatus status;

    Date createdAt;

    Date paidAt;

    Date servedAt;

    @Column(unique = true)
    String stripeIntentId;

    @Column(unique = true)
    String stripeSessionId;

    Double totalPrice;

    @OneToMany(cascade = CascadeType.ALL)
    List<OrderItem> items;
}
