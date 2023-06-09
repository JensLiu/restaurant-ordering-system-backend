package dev.jens.menu.repository;

import dev.jens.menu.modal.MenuItemFlavour;
import dev.jens.menu.modal.MenuItemSize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemFlavourRepository extends JpaRepository<MenuItemFlavour, Long> {
}
