package com.jensdev.order.modal;

import com.jensdev.menu.modal.MenuItem;
import com.jensdev.menu.modal.MenuItemFlavour;
import com.jensdev.menu.modal.MenuItemSize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "size_id")
    MenuItemSize size;

    @ManyToOne
    @JoinColumn(name = "flavour_id", updatable = false)
    MenuItemFlavour flavour;

    Long quantity;

    @ManyToOne
    @JoinColumn(name = "menu_item_id", updatable = false)
    MenuItem menuItem;
}
