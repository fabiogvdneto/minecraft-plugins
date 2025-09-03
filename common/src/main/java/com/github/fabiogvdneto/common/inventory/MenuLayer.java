package com.github.fabiogvdneto.common.inventory;

import org.bukkit.entity.Player;

public interface MenuLayer {

    void init(MenuBuilder builder);

    void render(MenuBuilder builder, Player viewer);

}
