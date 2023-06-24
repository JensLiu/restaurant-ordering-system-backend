package com.jensdev.notification.dto;

import com.jensdev.order.modal.Order;
import com.jensdev.order.modal.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationDto implements BaseNotificationDto {

    Long orderId;
    OrderStatus orderStatus;

    @Override
    public NotificationType getType() {
        return NotificationType.ORDER;
    }

    public static OrderNotificationDto fromDomain(Order order) {
        return OrderNotificationDto.builder()
                .orderId(order.getId())
                .orderStatus(order.getStatus())
                .build();
    }

}
