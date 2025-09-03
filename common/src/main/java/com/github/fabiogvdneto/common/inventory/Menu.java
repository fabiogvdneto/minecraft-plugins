package com.github.fabiogvdneto.common.inventory;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class Menu {

    private final List<MenuLayer> layers;

    public Menu() {
        this.layers = new LinkedList<>();
    }

    public void addFirstLayer(MenuLayer layer) {
        layers.addFirst(layer);
    }

    public void addLayer(MenuLayer layer) {
        layers.add(layer);
    }

    public void open(Player player) {

    }
}
