package com.jensdev.menu.repository;

import com.jensdev.menu.modal.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    @Query("select m from MenuItem m where m.isDeleted = false")
    List<MenuItem> findAllNotDeleted();
}
