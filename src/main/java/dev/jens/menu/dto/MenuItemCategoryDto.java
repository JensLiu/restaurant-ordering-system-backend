package dev.jens.menu.dto;

import dev.jens.menu.modal.MenuItemCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemCategoryDto {
    Long id;
    String value;
    public MenuItemCategory toDomain() {
        return MenuItemCategory.builder()
                .id(id)
                .value(value)
                .build();
    }

    public static MenuItemCategoryDto fromDomain(MenuItemCategory category) {
        return MenuItemCategoryDto.builder()
                .id(category.getId())
                .value(category.getValue())
                .build();
    }
}
