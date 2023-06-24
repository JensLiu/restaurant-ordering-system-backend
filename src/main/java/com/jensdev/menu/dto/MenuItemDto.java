package com.jensdev.menu.dto;

import com.jensdev.menu.modal.MenuItem;
import com.jensdev.menu.modal.MenuItemCategory;
import com.jensdev.menu.modal.MenuItemFlavour;
import com.jensdev.menu.modal.MenuItemSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDto {
    Long id;
    String name;
    String description;
    String imageSrc;
    List<MenuItemFlavourDto> flavours;
    List<MenuItemSizesDto> sizes;
    List<MenuItemCategoryDto> categories;
    Boolean isSoldOut;

    public MenuItem toDomain() {
        List<MenuItemFlavour> domainFlavours = this.flavours.stream().map(MenuItemFlavourDto::toDomain).toList();
        List<MenuItemSize> domainSizes = this.sizes.stream().map(MenuItemSizesDto::toDomain).toList();
        List<MenuItemCategory> domainCategories = this.categories.stream().map(MenuItemCategoryDto::toDomain).toList();
        return MenuItem.builder()
                .id(id)
                .name(name)
                .description(description)
                .imageSrc(imageSrc)
                .flavours(domainFlavours)
                .sizes(domainSizes)
                .categories(domainCategories)
                .isSoldOut(isSoldOut)
                .isDeleted(false)
                .build();
    }

    public static MenuItemDto fromDomain(MenuItem item) {
        List<MenuItemFlavourDto> dtoFlavours = item.getFlavours().stream().map(MenuItemFlavourDto::fromDomain).toList();
        List<MenuItemSizesDto> dtoSizes = item.getSizes().stream().map(MenuItemSizesDto::fromDomain).toList();
        List<MenuItemCategoryDto> dtoCategories = item.getCategories().stream().map(MenuItemCategoryDto::fromDomain).toList();
        return MenuItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .imageSrc(item.getImageSrc())
                .flavours(dtoFlavours)
                .sizes(dtoSizes)
                .categories(dtoCategories)
                .isSoldOut(item.getIsSoldOut())
                .build();
    }

}

