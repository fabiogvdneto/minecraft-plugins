package com.github.fabiogvdneto.common.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Menu implements InventoryHolder {

    private static final MenuAction DEFAULT_ACTION = MenuAction.doNothing();

    public static int flatten(int row, int column) {
        return (row - 1) * 9 + (column - 1);
    }

    private final Inventory inventory;
    private final MenuAction[] actions;

    public Menu(int size) {
        this.inventory = Bukkit.createInventory(this, size);
        this.actions = createActions(size);
    }

    public Menu(int size, Component title) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.actions = createActions(size);
    }

    public Menu(InventoryType type) {
        this.inventory = Bukkit.createInventory(this, type);
        this.actions = createActions(inventory.getSize());
    }

    public Menu(InventoryType type, Component title) {
        this.inventory = Bukkit.createInventory(this, type, title);
        this.actions = createActions(inventory.getSize());
    }

    private MenuAction[] createActions(int size) {
        MenuAction[] slots = new MenuAction[size];
        Arrays.fill(slots, DEFAULT_ACTION);
        return slots;
    }

    /* ---- Inventory ---- */

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    /* ---- Event ---- */

    public void accept(InventoryClickEvent event) {
        actions[event.getSlot()].accept(event);
    }

    /* ---- Items and Actions ---- */

    public Menu withItem(int row, int column, ItemStack item) {
        return withItem(flatten(row, column), item);
    }

    public Menu withItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
        return this;
    }

    public Menu withItem(int row, int column, ItemStack item, MenuAction action) {
        return withItem(flatten(row, column), item, action);
    }

    public Menu withItem(int slot, ItemStack item, MenuAction action) {
        return withItem(slot, item).withAction(slot, action);
    }

    public Menu withAction(int row, int column, MenuAction action) {
        return withAction(flatten(row, column), action);
    }

    public Menu withAction(int slot, MenuAction action) {
        actions[slot] = (action == null) ? DEFAULT_ACTION : action;
        return this;
    }

    public MenuAction getAction(int row, int column) {
        return actions[flatten(row, column)];
    }

    public MenuAction getAction(int slot) {
        return actions[slot];
    }

    public void clear(int row, int column) {
        clear(flatten(row, column));
    }

    public void clear(int slot) {
        this.inventory.clear(slot);
        this.actions[slot] = MenuAction.doNothing();
    }
}
