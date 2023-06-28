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
public class OrderMessageDto implements BaseMessageDto {

    Long orderId;
    OrderStatus orderStatus;

    @Override
    public NotificationType getType() {
        return NotificationType.ORDER;
    }

    public static OrderMessageDto fromDomain(Order order) {
        return OrderMessageDto.builder()
                .orderId(order.getId())
                .orderStatus(order.getStatus())
                .build();
    }

}
