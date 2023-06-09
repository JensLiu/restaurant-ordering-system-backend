package dev.jens.test;

import dev.jens.menu.modal.MenuItem;
import dev.jens.menu.modal.MenuItemFlavour;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        MenuItem.builder().flavours(List.of(MenuItemFlavour.builder().build())).build();
    }
}
