package com.jensdev.menu.service;

import com.jensdev.common.businessException.BusinessException;
import com.jensdev.menu.modal.MenuItem;
import com.jensdev.menu.modal.MenuItemCategory;
import com.jensdev.menu.modal.MenuItemFlavour;
import com.jensdev.menu.modal.MenuItemSize;
import com.jensdev.menu.repository.MenuCategoryRepository;
import com.jensdev.menu.repository.MenuItemFlavourRepository;
import com.jensdev.menu.repository.MenuItemSizeRepository;
import com.jensdev.menu.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Log4j2
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
    public List<MenuItem> getAvailableMenuItems() {
        return menuRepository.findAllAvailable();
    }

    @Override
    public MenuItem updateMenuItem(MenuItem menuItem) {

        MenuItem old = menuRepository.findById(menuItem.getId()).orElseThrow(() -> new BusinessException("Cannot update non-existent item"));

        Set<MenuItemFlavour> deletingFlavours = new HashSet<>(old.getFlavours());
        Set<MenuItemSize> deletingSizes = new HashSet<>(old.getSizes());

        menuItem.getFlavours().forEach(deletingFlavours::remove);
        menuItem.getSizes().forEach(deletingSizes::remove);

        MenuItem item = menuRepository.save(menuItem);

        try {
            log.info("deleting flavours: " + deletingFlavours);
            log.info("deleting sizes: " + deletingSizes);
            // deleteAllByIds does not report if it fails
            menuItemFlavourRepository.deleteAllInBatch(deletingFlavours);
            menuItemSizeRepository.deleteAllInBatch(deletingSizes);
        } catch (DataIntegrityViolationException e) {
            log.info("Some flavour/size might not be removed from database because it already has a reference to it");
            log.info("rolling back changes");
            deletingFlavours.addAll(item.getFlavours());
            deletingSizes.addAll(item.getSizes());
            item.setFlavours(deletingFlavours.stream().toList());
            item.setSizes(deletingSizes.stream().toList());
            System.out.println(item.getFlavours());
            System.out.println(item.getSizes());
            menuRepository.save(item);
            throw new BusinessException("Cannot delete flavour/size because it is used in other places");
        }
        return item;
    }

    @Override
    public void deleteItem(Long id) {
        try {
            menuRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            log.info("Cannot delete item because it is used in other places");
            throw new BusinessException("Cannot delete item because it is used in other places");
//            MenuItem item = menuRepository.findById(id).orElseThrow(() -> new BusinessException("Cannot delete non-existent item"));
//            item.setIsDeleted(true);
//            menuRepository.save(item);
        }
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
