package com.jensdev.order.dto;


import com.jensdev.order.modal.Order;
import com.jensdev.order.modal.OrderStatus;
import com.jensdev.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    Long id;
    String pickupCode;
    UserDto user;

    Date createdAt;
    Date paidAt;
    Date servedAt;
    Double totalPrice;
    OrderStatus status;
    List<OrderItemDto> items;

    public static OrderDto fromDomain(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .pickupCode(order.getPickupCode())
                .user(UserDto.fromDomain(order.getUser()))
                .createdAt(order.getCreatedAt())
                .paidAt(order.getPaidAt())
                .servedAt(order.getServedAt())
                .totalPrice(order.getTotalPrice())
                .items(order.getItems().stream().map(OrderItemDto::fromDomain).toList())
                .status(order.getStatus())
                .build();
    }

}
