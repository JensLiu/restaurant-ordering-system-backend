package dev.jens.menu.dto;

import dev.jens.menu.modal.MenuItemSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemSizesDto {
    String id;
    String size;
    Double price;

    public MenuItemSize toDomain() {
        return MenuItemSize.builder()
                .id(id == null || id.equals("") ? null : Long.parseLong(id))
                .size(size)
                .unitPrice(price)
                .build();
    }

    public static MenuItemSizesDto fromDomain(MenuItemSize size) {
        return MenuItemSizesDto.builder()
                .id(String.valueOf(size.getId()))
                .size(size.getSize())
                .price(size.getUnitPrice())
                .build();
    }
}
