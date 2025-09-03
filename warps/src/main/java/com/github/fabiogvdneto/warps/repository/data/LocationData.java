package com.github.fabiogvdneto.warps.repository.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;
import java.util.UUID;

public record LocationData(
        String worldId,
        double x,
        double y,
        double z,
        float yaw,
        float pitch
) implements Serializable {

    public LocationData(Location loc) {
        this(
                loc.getWorld().getUID().toString(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getYaw(),
                loc.getPitch()
        );
    }

    public Location bukkit() {
        return new Location(world(), x, y, z, yaw, pitch);
    }

    private World world() {
        try {
            return Bukkit.getWorld(UUID.fromString(worldId));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
