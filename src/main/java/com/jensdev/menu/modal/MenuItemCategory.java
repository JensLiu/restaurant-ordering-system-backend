package com.jensdev.menu.modal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu_item_category")
public class MenuItemCategory {
    @Id
    @GeneratedValue
    Long id;
    String value;
}
