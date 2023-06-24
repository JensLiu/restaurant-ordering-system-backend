package com.jensdev.order.dto;

import com.jensdev.menu.dto.MenuItemDto;
import com.jensdev.menu.dto.MenuItemFlavourDto;
import com.jensdev.menu.dto.MenuItemSizesDto;
import com.jensdev.order.modal.OrderItem;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    Long id;
    MenuItemSizesDto size;
    MenuItemFlavourDto flavour;
    MenuItemDto menuItem;
    Long quantity;
    Double subtotal;

    public static OrderItemDto fromDomain(OrderItem orderItem) {
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .size(MenuItemSizesDto.fromDomain(orderItem.getSize()))
                .flavour(MenuItemFlavourDto.fromDomain(orderItem.getFlavour()))
                .menuItem(MenuItemDto.fromDomain(orderItem.getMenuItem()))
                .quantity(orderItem.getQuantity())
                .subtotal(orderItem.getSize().getUnitPrice() * orderItem.getQuantity())
                .build();
    }

}
