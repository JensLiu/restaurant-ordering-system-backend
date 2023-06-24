package com.jensdev.menu.dto;

import com.jensdev.menu.modal.MenuItemSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemSizesDto {
    Long id;
    String size;
    Double price;

    public MenuItemSize toDomain() {
        return MenuItemSize.builder()
                .id(id)
                .size(size)
                .unitPrice(price)
                .build();
    }

    public static MenuItemSizesDto fromDomain(MenuItemSize size) {
        return MenuItemSizesDto.builder()
                .id(size.getId())
                .size(size.getSize())
                .price(size.getUnitPrice())
                .build();
    }
}
