package com.jensdev.menu.modal;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "menu_item")
public class MenuItem {
    @Id
    @GeneratedValue
    Long id;
    String name;
    String description;
    String imageSrc;
    Boolean isSoldOut;
    Boolean isDeleted;

    @OneToMany(cascade=CascadeType.ALL)
    List<MenuItemFlavour> flavours;

    @OneToMany(cascade=CascadeType.ALL)
    List<MenuItemSize> sizes;

    @ManyToMany
    List<MenuItemCategory> categories;

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageSrc='" + imageSrc + '\'' +
                ", isSoldOut=" + isSoldOut +
                ", isDeleted=" + isDeleted +
                ", flavours=" + flavours +
                ", sizes=" + sizes +
                '}';
    }
}
