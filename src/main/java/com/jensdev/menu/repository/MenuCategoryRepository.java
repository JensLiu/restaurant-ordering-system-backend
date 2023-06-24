package com.jensdev.menu.repository;

import com.jensdev.menu.modal.MenuItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuCategoryRepository extends JpaRepository<MenuItemCategory, Long> {

}
