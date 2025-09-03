package com.github.fabiogvdneto.common.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collection;

@FunctionalInterface
public interface MenuAction {

    void accept(InventoryClickEvent event);

    static MenuAction doNothing() {
        return (event) -> { /* do nothing */ };
    }

    static MenuAction cancelEvent() {
        return (event) -> event.setCancelled(true);
    }

    static MenuAction playerCommand(String name) {
        return (event) -> ((Player) event.getWhoClicked()).performCommand(name);
    }

    static MenuAction dispatchCommand(String name) {
        return (event) -> Bukkit.dispatchCommand(event.getWhoClicked(), name);
    }

    static MenuAction switchMenu(Menu menu) {
        return (event) -> menu.open((Player) event.getWhoClicked());
    }

    static MenuAction doAll(Collection<MenuAction> actions) {
        return (event) -> {
            for (MenuAction action : actions) {
                action.accept(event);
            }
        };
    }

    static MenuAction doAll(MenuAction... actions) {
        return (event) -> {
            for (MenuAction action : actions) {
                action.accept(event);
            }
        };
    }
}
