package com.github.fabiogvdneto.common.inventory;

import com.github.fabiogvdneto.common.menu.MenuAction;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuBuilder {

    private final Component title;
    private final InventoryType type;
    private final int size;

    private final ItemStack[] items;
    private final MenuAction[] actions;

    public MenuBuilder(Component title, InventoryType type) {
        this(title, type, type.getDefaultSize());
    }

    public MenuBuilder(Component title, int size) {
        this(title, null, size);
    }

    private MenuBuilder(Component title, InventoryType type, int size) {
        this.size = size;
        this.type = type;
        this.title = title;
        this.items = new ItemStack[size];
        this.actions = new MenuAction[size];
    }

    /* ---- Set (Fixed Position) ---- */

    public MenuBuilder setItem(int row, int col, ItemStack item) {
        return setItem(slot(row, col), item);
    }

    public MenuBuilder setItem(int slot, ItemStack item) {
        this.items[slot] = item;
        return this;
    }

    public MenuBuilder setButton(int row, int col, ItemStack item, MenuAction action) {
        return setButton(slot(row, col), item, action);
    }

    public MenuBuilder setButton(int slot, ItemStack item, MenuAction action) {
        this.items[slot] = item;
        this.actions[slot] = action;
        return this;
    }

    public MenuBuilder setAction(int row, int col, MenuAction action) {
        return setAction(slot(row, col), action);
    }

    public MenuBuilder setAction(int slot, MenuAction action) {
        this.actions[slot] = action;
        return this;
    }

    private int slot(int row, int col) {
        return (row - 1) * size + (col - 1);
    }

    /* ---- Add (Dynamic Position) ---- */

    public MenuBuilder addItem(ItemStack item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = item;
            }
        }

        return this;
    }

    public MenuBuilder addButton(ItemStack item, MenuAction action) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = item;
                actions[i] = action;
            }
        }

        return this;
    }

    /* ---- Fill (Fixed Positions) ---- */

    public MenuBuilder fillRow(int row, ItemStack item) {
        int initialSlot = (row - 1) * 9;

        if (size > initialSlot) {
            for (int i = 0; i < 9; i++) {
                setItem(initialSlot + i, item);
            }
        }

        return this;
    }

    public MenuBuilder fillRow(int row, ItemStack item, MenuAction action) {
        int initialSlot = (row - 1) * 9;

        if (size > initialSlot) {
            for (int i = 0; i < 9; i++) {
                setButton(initialSlot + i, item, action);
            }
        }

        return this;
    }

    public MenuBuilder fillColumn(int col, ItemStack item) {
        int nextSlot = col - 1;

        while (nextSlot < size) {
            setItem(nextSlot, item);
            nextSlot += 9;
        }

        return this;
    }

    public MenuBuilder fillColumn(int col, ItemStack item, MenuAction action) {
        int nextSlot = col - 1;

        while (nextSlot < size) {
            setButton(nextSlot, item, action);
            nextSlot += 9;
        }

        return this;
    }

    public Inventory build() {
        Inventory inv = (type == null)
                ? Bukkit.createInventory(null, size, title)
                : Bukkit.createInventory(null, type, title);

        inv.setContents(items);

        return inv;
    }
}
