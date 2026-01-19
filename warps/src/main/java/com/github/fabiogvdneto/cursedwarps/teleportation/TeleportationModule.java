package com.github.fabiogvdneto.cursedwarps.teleportation;

import com.github.fabiogvdneto.common.Plugins;
import com.github.fabiogvdneto.common.command.CommandBlocker;
import com.github.fabiogvdneto.common.module.PluginModule;
import com.github.fabiogvdneto.cursedwarps.WarpPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TeleportationModule implements TeleportationService, PluginModule {

    private final WarpPlugin plugin;
    private final Map<UUID, Teleportation> ongoing = new HashMap<>();

    private CommandBlocker commandBlocker;

    public TeleportationModule(WarpPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    /* ---- Service ---- */

    @Override
    public Teleportation get(UUID playerId) {
        return ongoing.get(playerId);
    }

    @Override
    public Teleportation get(Player player) {
        return ongoing.get(player.getUniqueId());
    }

    @Override
    public void teleport(Player player, Player target) {
        teleport(player, () -> {
            if (player.isOnline()) {
                if (target.isOnline()) {
                    return target.getLocation();
                }
                plugin.getMessages().teleportationCancelled(player);
            }
            return null;
        });
    }

    @Override
    public void teleport(Player player, Location target) {
        teleport(player, () -> target);
    }

    @Override
    public void teleport(Player player, Supplier<Location> target) {
        int delay = plugin.getSettings().getTeleportationDelay(player);

        TeleportationImpl teleportation = new TeleportationImpl(player, target, delay);

        // 1. Check for player movement.
        if (!plugin.getSettings().isMovementAllowedWhileTeleporting()) {
            teleportation.onMovement(task -> {
                task.cancel();
                plugin.getMessages().movementNotAllowedDuringTeleportation(task.getPlayer());
            });
        }

        // 2. Check for player damage.
        if (!plugin.getSettings().isDamageAllowedWhileTeleporting()) {
            teleportation.onDamage(task -> {
                task.cancel();
                plugin.getMessages().damageNotAllowedDuringTeleportation(task.getPlayer());
            });
        }

        // 3. Give feedback to the player on countdown.
        teleportation.onCountdown(task ->
                plugin.getMessages().teleportationCountdown(task.getPlayer(), task.getCounter())
        );

        teleportation.begin();
    }

    /* ---- Module ---- */

    private void disableCommandBlocker() {
        if (commandBlocker != null) {
            this.commandBlocker.unregister();
            this.commandBlocker = null;
        }
    }

    private void enableCommandBlocker() {
        final String mode = plugin.getSettings().getTeleportationCommandsAllowedMode();
        final Set<String> list = plugin.getSettings().getTeleportationCommandsAllowedList();
        final Predicate<String> commandFilter;

        switch (mode) {
            case "blacklist": // Block all commands in the list.
                commandFilter = list::contains;
                break;
            case "whitelist": // Block all commands not in the list.
                commandFilter = cmd -> !list.contains(cmd);
                break;
            case "false": // Block all commands.
                commandFilter = _ -> true;
                break;
            default: // Unknown value. All commands will be allowed.
                disableCommandBlocker();
                return;
        }

        if (this.commandBlocker == null) {
            this.commandBlocker = new CommandBlocker(ongoing.keySet(), commandFilter);
            this.commandBlocker.register(plugin);
        } else {
            this.commandBlocker.filter(commandFilter);
        }
    }

    @Override
    public void load() {
        enableCommandBlocker();
    }

    @Override
    public void unload() {
        disableCommandBlocker();
    }

    private class TeleportationImpl implements Teleportation, Runnable {

        private final Player player;
        private final Supplier<Location> target;
        private final List<Consumer<Teleportation>> observers = new LinkedList<>();

        private final int delay;

        private int counter;
        private BukkitTask task;
        private boolean cancelled;

        private TeleportationImpl(Player player, Supplier<Location> target, int delay) {
            this.player = Objects.requireNonNull(player);
            this.target = Objects.requireNonNull(target);
            this.delay = Math.max(0, delay);

            // A negative value indicates that this teleportation has not yet started.
            this.counter = -1;
        }

        @Override
        public Player getPlayer() {
            return player;
        }

        @Override
        public int getDelay() {
            return delay;
        }

        @Override
        public int getCounter() {
            return counter;
        }

        public void onCountdown(Consumer<Teleportation> observer) {
            observers.add(observer);
        }

        public void onMovement(Consumer<Teleportation> callback) {
            onCountdown(new Consumer<>() {
                private static final double THRESHOLD = 0.02;
                private Location prevLocation;

                @Override
                public void accept(Teleportation instance) {
                    Location currLocation = instance.getPlayer().getLocation();

                    if (prevLocation == null) {
                        prevLocation = currLocation.clone();
                    } else if (prevLocation.distanceSquared(currLocation) > THRESHOLD) {
                        callback.accept(instance);
                    }
                }
            });
        }

        public void onDamage(Consumer<Teleportation> callback) {
            onCountdown(new Consumer<>() {
                private double prevHealth;

                @Override
                public void accept(Teleportation instance) {
                    double currHealth = instance.getPlayer().getHealth();

                    if (currHealth < prevHealth) {
                        callback.accept(instance);
                    }

                    // Watch out for health regeneration.
                    this.prevHealth = currHealth;
                }
            });
        }

        public void teleportNow() {
            Location destination = target.get();

            if (destination != null) {
                player.teleportAsync(destination);
            }
        }

        public void begin() throws IllegalStateException {
            if (counter > 0)
                throw new IllegalStateException("teleportation cannot happen twice");

            this.counter = delay;

            if (notifyObservers() == -1) {
                return;
            }

            if (delay == 0) {
                teleportNow();
                return;
            }

            this.task = Plugins.sync(plugin, this, /* delay */ 20L, /* period */ 20L);

            // Register this teleportation instance.
            ongoing.put(player.getUniqueId(), this);
        }

        @Override
        public void cancel() throws IllegalStateException {
            if (cancelled) {
                throw new IllegalStateException("teleportation cannot be cancelled twice");
            }

            this.cancelled = true;

            if (task != null) {
                task.cancel();
                ongoing.remove(player.getUniqueId());
            }
        }

        @Override
        public void run() {
            this.counter--;

            if (notifyObservers() == 0) {
                cancel();
                teleportNow();
            }
        }

        private int notifyObservers() {
            for (Consumer<Teleportation> observer : observers) {
                observer.accept(this);

                if (cancelled) return -1;
            }

            return counter;
        }
    }
}
