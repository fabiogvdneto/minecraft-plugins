package com.github.fabiogvdneto.common.teleporter;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PluginTeleporter implements Teleporter {

    private final Plugin plugin;
    private final Map<UUID, Teleportation> ongoing = new HashMap<>();
    private CommandBlocker commandBlocker;

    public PluginTeleporter(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public Collection<Teleportation> ongoing() {
        return Collections.unmodifiableCollection(ongoing.values());
    }

    @Override
    public Teleportation ongoing(Player recipient) {
        return ongoing.get(recipient.getUniqueId());
    }

    @Override
    public Teleportation create(Player recipient, Location dest) {
        return create(recipient, () -> dest);
    }

    @Override
    public Teleportation create(Player recipient, Supplier<Location> dest) {
        return new TeleportationImpl(recipient, dest);
    }

    public void setCommandFilter(Predicate<String> filter) {
        if (commandBlocker != null) {
            PlayerCommandPreprocessEvent.getHandlerList().unregister(commandBlocker);
            this.commandBlocker = null;
        }

        if (filter != null) {
            this.commandBlocker = new CommandBlocker(ongoing.keySet(), filter);
            plugin.getServer().getPluginManager().registerEvents(commandBlocker, plugin);
        }
    }

    private class TeleportationImpl extends BukkitRunnable implements Teleportation {

        private final Player recipient;
        private final Supplier<Location> destination;
        private final List<Consumer<Teleportation>> observers = new LinkedList<>();

        private int delay;
        private int counter;

        private TeleportationImpl(Player recipient, Supplier<Location> destination) {
            this.recipient = recipient;
            this.destination = destination;
            this.delay = 0;
            // A negative value indicates that this teleportation has not yet started or has already ended.
            this.counter = -1;
        }

        @Override
        public Player getRecipient() {
            return recipient;
        }

        @Override
        public Location getDestination() {
            return destination.get();
        }

        @Override
        public int getDelay() {
            return delay;
        }

        @Override
        public int getCounter() {
            return counter;
        }

        @Override
        public void begin() throws IllegalStateException {
            if (counter > 0)
                throw new IllegalStateException("the teleportation has already started");

            this.counter = delay;

            if (counter > 0) {
                register();
                runTaskTimer(plugin, 0L, 20L);
            } else {
                updateObservers();
                teleportNow();
                this.counter = -1;
            }
        }

        private void register() {
            Teleportation previous = ongoing.put(recipient.getUniqueId(), this);

            if (previous != null) {
                previous.cancel();
            }
        }

        @Override
        public void cancel() throws IllegalStateException {
            if (delay <= 0)
                throw new IllegalStateException("teleportation can't be cancelled because it has no delay");

            super.cancel();
            unregister();
        }

        private void unregister() {
            ongoing.remove(recipient.getUniqueId());
        }

        @Override
        public void run() {
            if (updateObservers() && counter-- == 0) {
                cancel();
                teleportNow();
            }
        }

        private boolean updateObservers() {
            boolean scheduled = delay > 0;

            for (Consumer<Teleportation> callback : observers) {
                callback.accept(this);

                // Ensure the task was scheduled before checking if it was cancelled.
                if (scheduled && isCancelled()) return false;
            }

            return true;
        }

        private void teleportNow() {
            Location location = destination.get();

            if (location != null) {
                recipient.teleport(location);
            }
        }

        @Override
        public Teleportation withDelay(int delay) {
            this.delay = Math.max(0, delay);
            return this;
        }

        @Override
        public Teleportation onCountdown(Consumer<Teleportation> callback) {
            observers.add(callback);
            return this;
        }
    }

    private record CommandBlocker(Set<UUID> targets, Predicate<String> filter) implements Listener {
        @EventHandler
        public void onEvent(PlayerCommandPreprocessEvent e) {
            if (!targets.contains(e.getPlayer().getUniqueId())) return;

            String msg = e.getMessage();

            if (msg.charAt(0) != '/') return;

            int end = msg.indexOf(' ');

            if (filter.test((end == -1) ? msg.substring(1) : msg.substring(1, end))) {
                e.setCancelled(true);
            }
        }
    }
}
