package com.jensdev.menu.dto;

import com.jensdev.menu.modal.MenuItemFlavour;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemFlavourDto {
    Long id;
    String name;

    public MenuItemFlavour toDomain() {
        return MenuItemFlavour.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static MenuItemFlavourDto fromDomain(MenuItemFlavour flavour) {
        return MenuItemFlavourDto.builder()
                .id(flavour.getId())
                .name(flavour.getName())
                .build();
    }
}
