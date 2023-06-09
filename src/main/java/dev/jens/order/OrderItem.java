package dev.jens.order;

import dev.jens.menu.modal.MenuItem;
import dev.jens.menu.modal.MenuItemFlavour;
import dev.jens.menu.modal.MenuItemSize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.springframework.data.repository.cdi.Eager;

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
