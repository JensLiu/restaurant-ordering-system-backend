package com.jensdev.menu.controller;

import com.jensdev.menu.service.MenuService;
import com.jensdev.menu.dto.MenuItemCategoryDto;
import com.jensdev.menu.dto.MenuItemDto;
import com.jensdev.menu.modal.MenuItem;
import com.jensdev.menu.modal.MenuItemCategory;
import com.jensdev.user.modal.Role;
import com.jensdev.user.modal.User;
import com.jensdev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Log4j2
public class MenuItemController {
    private final UserService userService;
    private final MenuService menuService;

    @PostMapping("/menu")
    ResponseEntity<MenuItemDto> uploadMenuItem(@RequestBody MenuItemDto uploadMenuItemDto, Authentication authentication) {
        log.info("uploading menu item: " + uploadMenuItemDto);
//        User user = userService.getUserOrElseThrow(authentication);
//        if (user.getRole() != Role.ADMIN) {
//            return ResponseEntity.badRequest().build();
//        }
        MenuItem item = uploadMenuItemDto.toDomain();
        log.info("before uploading: " + item);
        MenuItem menuItem = menuService.addMenuItem(item);
        log.info("after uploading: " + menuItem);
        return ResponseEntity.ok(MenuItemDto.fromDomain(menuItem));
    }

    @PostMapping("/menu/{id}")
    ResponseEntity<MenuItemDto> updateMenuItem(@PathVariable Long id, @RequestBody MenuItemDto uploadMenuItemDto, Authentication authentication) {

//        User user = userService.getUserOrElseThrow(authentication);
//        if (user.getRole() != Role.ADMIN) {
//            return ResponseEntity.badRequest().build();
//        }
        MenuItem item = uploadMenuItemDto.toDomain();
        item.setId(id);
        log.info("domain item to update: " + item);
        MenuItem menuItem = menuService.updateMenuItem(item);
        return ResponseEntity.ok(MenuItemDto.fromDomain(menuItem));
    }

    @DeleteMapping("/menu/{id}")
    ResponseEntity<Boolean> deleteMenuItem(@PathVariable Long id, Authentication authentication) {

//        User user = userService.getUserOrElseThrow(authentication);
//        if (user.getRole() != Role.ADMIN) {
//            return ResponseEntity.badRequest().build();
//        }

        menuService.deleteItem(id);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/menu/{id}/soldout")
    ResponseEntity<Boolean> soldOutMenuItem(@PathVariable Long id, Authentication authentication) {
        menuService.setIsSoldOut(id, true);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/menu/{id}/enable")
    ResponseEntity<Boolean> enableMenuItem(@PathVariable Long id, Authentication authentication) {
        menuService.setIsSoldOut(id, false);
        return ResponseEntity.ok(true);
    }

    @GetMapping(value = {
            "/menu",
            "/public/menu"
    })
    ResponseEntity<List<MenuItemDto>> getMenuItems() {
        List<MenuItem> items = menuService.getMenuItems();
        List<MenuItemDto> dtos = items.stream().map(MenuItemDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = {
            "/menu/{id}",
            "/public/menu/{id}"
    })
    ResponseEntity<MenuItemDto> getMenuItem(@PathVariable Long id) {
        MenuItem item = menuService.getMenuItem(id);
        return ResponseEntity.ok(MenuItemDto.fromDomain(item));
    }

    @GetMapping(value = {
            "/menu/categories",
            "/public/menu/categories"
    })
    ResponseEntity<List<MenuItemCategoryDto>> getCategories() {
        List<MenuItemCategory> categories = menuService.getCategories();
        List<MenuItemCategoryDto> dtos = categories.stream().map(MenuItemCategoryDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/menu/categories/{id}")
    ResponseEntity<Boolean> deleteCategory(@PathVariable Long id, Authentication authentication) {
        User user = userService.getUser(authentication);
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().build();
        }
        menuService.deleteCategory(id);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/menu/categories/{id}")
    ResponseEntity<MenuItemCategoryDto> updateCategory(@PathVariable Long id, @RequestBody MenuItemCategoryDto categoryDto, Authentication authentication) {
        User user = userService.getUser(authentication);
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().build();
        }
        MenuItemCategory category = categoryDto.toDomain();
        category.setId(id);
        MenuItemCategory updatedCategory = menuService.updateCategory(category);
        return ResponseEntity.ok(MenuItemCategoryDto.fromDomain(updatedCategory));
    }

    @PostMapping("/menu/categories")
    ResponseEntity<MenuItemCategoryDto> createCategory(@RequestBody MenuItemCategoryDto categoryDto, Authentication authentication) {
        User user = userService.getUser(authentication);
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().build();
        }
        MenuItemCategory category = categoryDto.toDomain();
        category.setId(null);
        MenuItemCategory createdCategory = menuService.addCategory(category);
        return ResponseEntity.ok(MenuItemCategoryDto.fromDomain(createdCategory));
    }


}
