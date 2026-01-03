package com.github.fabiogvdneto.warps.user;

import com.github.fabiogvdneto.common.PluginService;
import com.github.fabiogvdneto.common.Plugins;
import com.github.fabiogvdneto.common.repository.PluginCache;
import com.github.fabiogvdneto.warps.WarpsPlugin;
import com.github.fabiogvdneto.warps.repository.UserRepository;
import com.github.fabiogvdneto.warps.repository.data.UserData;
import com.github.fabiogvdneto.warps.repository.gson.GsonUserRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class UserService implements UserManager, PluginService {

    private final WarpsPlugin plugin;
    private Listener playerJoinListener;

    private PluginCache<UUID, User> cache;
    private UserRepository repository;
    private BukkitTask autosaveTask;

    public UserService(WarpsPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    /* ---- CACHE ---- */

    @Override
    public Collection<User> getAll() {
        return cache.getAll();
    }

    @Override
    public User get(UUID userId) {
        return cache.get(userId);
    }

    @Override
    public CompletableFuture<User> fetch(UUID userId) {
        return cache.fetch(userId, () -> {
            try {
                UserData data = repository.fetchOne(userId);
                return (data == null) ? new SimpleUser(userId) : new SimpleUser(data);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to load user data.", e);
                plugin.getLogger().warning("User data seems to be corrupted. It will be overwritten.");
                return new SimpleUser(userId);
            }
        });
    }

    /**
     * Can be combined with save() to safely store all user data to the repository asynchronously.
     * @return a snapshot of all users present in the cache
     */
    private List<UserData> memento() {
        return this.cache.getAll().stream().map(user -> ((SimpleUser) user).memento()).toList();
    }

    /**
     * Purges all offline players from the cache.
     * Data must be saved first to avoid data loss.
     */
    private void purgeCache() {
        Set<UUID> onlinePlayers = this.plugin.getServer().getOnlinePlayers()
                .stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toUnmodifiableSet());

        this.cache.getKeys().retainAll(onlinePlayers);
    }

    /* ---- PERSISTENCE ---- */

    /**
     * Attempts to create a new repository.
     */
    private void createRepository() {
        Path path = this.plugin.getDataPath().resolve("users");

        try {
            this.repository = new GsonUserRepository(path);
            this.repository.create();
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to create the user repository.", e);
        }
    }

    /**
     * Purges old data from the repository.
     */
    private void purgeRepository() {
        try {
            this.repository.purge(plugin.getSettings().getUserPurgeDays());
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to purge user data from the repository.", e);
        }
    }

    /**
     * Loads user data from all online players to the cache
     */
    private void loadOnlinePlayers() {
        Player[] snapshot = this.plugin.getServer().getOnlinePlayers().toArray(Player[]::new);

        for (Player player : snapshot) {
            this.fetch(player.getUniqueId());
        }
    }

    /**
     * Saves all user data to the repository
     * @param snapshot data to be stored
     */
    private void save(Collection<UserData> snapshot) {
        int successCount = 0;
        int errorCount = 0;

        for (UserData data : snapshot) {
            try {
                this.repository.storeOne(data.uid(), data);
                successCount++;
            } catch (Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to store user data (uuid=" + data.uid() + ").", e);
                errorCount++;
            }
        }

        this.plugin.getLogger().info("Saved " + successCount + " users with " + errorCount + " errors.");
    }

    private void autosave() {
        int ticks = this.plugin.getSettings().getUserAutosaveInterval() * 60 * 20;

        if (ticks > 0) {
            this.autosaveTask = Plugins.sync(plugin, () -> {
                // Copy cached data so that it can be stored asynchronously without race conditions.
                Collection<UserData> snapshot = this.memento();

                // We can now save the snapshot asynchronously.
                Plugins.async(plugin, () -> this.save(snapshot));

                // Remove (purge) offline players from the cache.
                this.purgeCache();
            }, ticks, ticks);
        }
    }

    /* ---- BUKKIT ---- */

    private void registerEvents() {
        this.playerJoinListener = new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                fetch(event.getPlayer().getUniqueId());
            }
        };

        this.plugin.getServer().getPluginManager().registerEvents(playerJoinListener, plugin);
    }

    /* ---- SERVICE ---- */

    @Override
    public void enable() {
        if (repository != null) {
            // Service is already enabled.
            return;
        }

        this.cache = new PluginCache<>(plugin);
        this.createRepository();
        this.purgeRepository();
        this.registerEvents();
        this.loadOnlinePlayers();
        this.autosave();
    }

    @Override
    public void disable() {
        if (repository == null) {
            // Service is already disabled.
            return;
        }

        this.autosaveTask.cancel();
        this.autosaveTask = null;

        PlayerJoinEvent.getHandlerList().unregister(playerJoinListener);
        this.playerJoinListener = null;

        save(memento());
        this.cache.clear();
        this.cache = null;
        this.repository = null;
    }
}
