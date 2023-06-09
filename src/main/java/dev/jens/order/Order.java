package dev.jens.order;

import dev.jens.user.User;
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

    String pickUpCode;

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

    @OneToMany
    List<OrderItem> items;
}
