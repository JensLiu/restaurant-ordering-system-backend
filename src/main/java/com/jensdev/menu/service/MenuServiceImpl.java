package com.jensdev.menu.service;

import com.jensdev.common.exceptions.BusinessException;
import com.jensdev.menu.modal.MenuItem;
import com.jensdev.menu.modal.MenuItemCategory;
import com.jensdev.menu.modal.MenuItemFlavour;
import com.jensdev.menu.modal.MenuItemSize;
import com.jensdev.menu.repository.MenuCategoryRepository;
import com.jensdev.menu.repository.MenuItemFlavourRepository;
import com.jensdev.menu.repository.MenuItemSizeRepository;
import com.jensdev.menu.repository.MenuItemRepository;
import com.jensdev.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuItemRepository menuRepository;
    public final MenuCategoryRepository menuCategoryRepository;
    public final MenuItemSizeRepository menuItemSizeRepository;
    public final MenuItemFlavourRepository menuItemFlavourRepository;

    @Override
    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuRepository.save(menuItem);
    }

    @Override
    public List<MenuItemCategory> getCategories() {
        return menuCategoryRepository.findAll();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return menuRepository.findAllNotDeleted();
    }

    @Override
    public MenuItem updateMenuItem(MenuItem menuItem) {
        MenuItem old = menuRepository.findById(menuItem.getId()).orElseThrow(() -> new BusinessException("Cannot update non-existent item"));

        List<Long> flavourIds = old.getFlavours().stream().map(MenuItemFlavour::getId).toList();
        List<Long> sizeIds = old.getSizes().stream().map(MenuItemSize::getId).toList();
        menuItemFlavourRepository.deleteAllById(flavourIds);
        menuItemSizeRepository.deleteAllById(sizeIds);

        return menuRepository.save(menuItem);
    }

    @Override
    public void deleteItem(Long id) {
        MenuItem item = menuRepository.findById(id).orElseThrow(() -> new BusinessException("Cannot delete non-existent item"));
        item.setIsDeleted(true);
        menuRepository.save(item);
    }

    @Override
    public void setIsSoldOut(Long id, Boolean isSoldOut) {
        MenuItem item = menuRepository.findById(id).orElseThrow(() -> new BusinessException("Cannot set sold out on non-existent item"));
        item.setIsSoldOut(isSoldOut);
        menuRepository.save(item);
    }

    @Override
    public MenuItem getMenuItem(Long id) {
        return menuRepository.findById(id).orElseThrow(() -> new BusinessException("Cannot find item"));
    }

    @Override
    public MenuItemCategory updateCategory(MenuItemCategory category) {
        return menuCategoryRepository.save(category);
    }

    @Override
    public MenuItemCategory addCategory(MenuItemCategory category) {
        category.setId(null);
        return menuCategoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        menuCategoryRepository.deleteById(id);
    }
}
