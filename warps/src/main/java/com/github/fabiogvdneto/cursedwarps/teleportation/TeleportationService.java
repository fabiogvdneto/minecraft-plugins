package com.github.fabiogvdneto.cursedwarps.teleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Supplier;

public interface TeleportationService {

    Teleportation get(UUID playerId);

    Teleportation get(Player player);

    void teleport(Player player, Player target);

    void teleport(Player player, Location target);

    void teleport(Player player, Supplier<Location> target);

}
