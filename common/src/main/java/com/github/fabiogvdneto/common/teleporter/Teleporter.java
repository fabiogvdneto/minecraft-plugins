package com.github.fabiogvdneto.common.teleporter;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.function.Supplier;

public interface Teleporter {

    Collection<Teleportation> ongoing();

    Teleportation ongoing(Player recipient);

    Teleportation create(Player recipient, Location dest);

    Teleportation create(Player recipient, Supplier<Location> dest);

}
