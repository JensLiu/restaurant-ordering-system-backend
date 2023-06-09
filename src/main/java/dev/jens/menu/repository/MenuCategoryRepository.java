package dev.jens.menu.repository;

import dev.jens.menu.modal.MenuItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuCategoryRepository extends JpaRepository<MenuItemCategory, Long> {

}
