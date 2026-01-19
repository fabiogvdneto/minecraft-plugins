package com.github.fabiogvdneto.common.command;

import com.github.fabiogvdneto.common.Plugins;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class CommandBlocker implements Listener {

    /**
     * Only block commands executed by these players.
     */
    private final Set<UUID> playerFilter;

    /**
     * Only block commands accepted by this filter.
     */
    private Predicate<String> commandFilter;

    public CommandBlocker(Set<UUID> playerFilter) {
        this(playerFilter, _ -> true);
    }

    public CommandBlocker(Set<UUID> playerFilter, Predicate<String> commandFilter) {
        this.playerFilter = Objects.requireNonNull(playerFilter);
        this.filter(commandFilter);
    }

    public void filter(Predicate<String> filter) {
        this.commandFilter = Objects.requireNonNull(filter);
    }

    public void register(Plugin plugin) {
        Plugins.registerEvents(plugin, this);
    }

    public void unregister() {
        PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
    }

    /* ---- Listener ---- */

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (playerFilter.contains(event.getPlayer().getUniqueId())) {
            // Extract command label from message.
            String message = event.getMessage();
            int spaceIndex = message.indexOf(' ');
            String command = (spaceIndex == -1) ? message.substring(1) : message.substring(1, spaceIndex);

            if (commandFilter.test(command)) {
                event.setCancelled(true);
            }
        }
    }
}
