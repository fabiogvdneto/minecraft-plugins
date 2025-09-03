package com.github.fabiogvdneto.common.teleporter;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface Teleportation {

    Player getRecipient();

    Location getDestination();

    int getDelay();

    int getCounter();

    void begin() throws IllegalStateException;

    void cancel() throws IllegalStateException;

    Teleportation withDelay(int delay);

    Teleportation onCountdown(Consumer<Teleportation> callback);

    default Teleportation onMovement(Consumer<Teleportation> callback) {
        return onCountdown(new Consumer<>() {
            private static final double THRESHOLD = 0.02;
            private Location location;

            @Override
            public void accept(Teleportation instance) {
                Location newLocation = instance.getRecipient().getLocation();

                if (location == null) {
                    location = newLocation.clone();
                } else if (location.distanceSquared(newLocation) > THRESHOLD) {
                    callback.accept(instance);
                }
            }
        });
    }

    default Teleportation onDamage(Consumer<Teleportation> callback) {
        return onCountdown(new Consumer<>() {
            private double healthTracker;

            @Override
            public void accept(Teleportation instance) {
                double health = instance.getRecipient().getHealth();

                if (health < healthTracker) {
                    callback.accept(instance);
                }

                healthTracker = health;
            }
        });
    }

}
