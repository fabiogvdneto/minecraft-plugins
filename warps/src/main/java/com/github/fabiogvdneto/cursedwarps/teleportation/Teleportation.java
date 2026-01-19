package com.github.fabiogvdneto.cursedwarps.teleportation;

import org.bukkit.entity.Player;

public interface Teleportation {

    Player getPlayer();

    int getDelay();

    int getCounter();

    void begin() throws IllegalStateException;

    void cancel() throws IllegalStateException;
}
