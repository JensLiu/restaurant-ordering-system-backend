package com.jensdev.menu.service;

import com.jensdev.menu.modal.MenuItem;
import com.jensdev.menu.modal.MenuItemCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MenuService {
    MenuItem addMenuItem(MenuItem menuItem);
    List<MenuItemCategory> getCategories();
    List<MenuItem> getMenuItems();
    List<MenuItem> getAvailableMenuItems();
    MenuItem updateMenuItem(MenuItem menuItem);
    void deleteItem(Long id);
    void setIsSoldOut(Long id, Boolean isSoldOut);
    MenuItem getMenuItem(Long id);
    MenuItemCategory updateCategory(MenuItemCategory category);
    MenuItemCategory addCategory(MenuItemCategory category);
    void deleteCategory(Long id);
}
