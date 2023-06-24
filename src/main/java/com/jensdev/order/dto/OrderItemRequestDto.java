package com.jensdev.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDto {
    Long itemId;
    Long flavourId;
    Long sizeId;
    Long quantity;

}
