package com.github.fabiogvdneto.cursedwarps.user;

import com.github.fabiogvdneto.common.Plugins;
import com.github.fabiogvdneto.common.module.PluginModule;
import com.github.fabiogvdneto.common.repository.PluginCache;
import com.github.fabiogvdneto.cursedwarps.WarpPlugin;
import com.github.fabiogvdneto.cursedwarps.repository.UserRepository;
import com.github.fabiogvdneto.cursedwarps.repository.data.UserData;
import com.github.fabiogvdneto.cursedwarps.repository.gson.GsonUserRepository;
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

public class UserModule implements UserService, PluginModule {

    private final WarpPlugin plugin;
    private Listener playerJoinListener;

    private PluginCache<UUID, User> cache;
    private UserRepository repository;
    private BukkitTask autosaveTask;

    public UserModule(WarpPlugin plugin) {
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
        return cache.load(userId, () -> {
            try {
                UserData data = repository.fetchOne(userId);
                return (data == null) ? new UserImpl(userId) : new UserImpl(data);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to load user data.", e);
                plugin.getLogger().warning("User data seems to be corrupted. It will be overwritten.");
                return new UserImpl(userId);
            }
        });
    }

    /**
     * Can be combined with save() to safely store all user data to the repository asynchronously.
     * @return a snapshot of all users present in the cache
     */
    private List<UserData> memento() {
        return cache.getAll().stream().map(user -> ((UserImpl) user).memento()).toList();
    }

    /**
     * Purges all offline players from the cache.
     * Data must be saved first to avoid data loss.
     */
    private void purgeCache() {
        Set<UUID> onlinePlayers = plugin.getServer().getOnlinePlayers()
                .stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toUnmodifiableSet());

        cache.getKeys().retainAll(onlinePlayers);
    }

    /* ---- PERSISTENCE ---- */

    /**
     * Attempts to create a new repository.
     */
    private void createRepository() {
        Path path = plugin.getDataPath().resolve("users");

        try {
            this.repository = new GsonUserRepository(path);
            this.repository.create();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to create the user repository.", e);
        }
    }

    /**
     * Purges old data from the repository.
     */
    private void purgeRepository() {
        try {
            repository.purge(plugin.getSettings().getUserPurgeDays());
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to purge user data from the repository.", e);
        }
    }

    /**
     * Loads user data from all online players to the cache
     */
    private void loadOnlinePlayers() {
        Player[] snapshot = plugin.getServer().getOnlinePlayers().toArray(Player[]::new);

        for (Player player : snapshot) {
            fetch(player.getUniqueId());
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
                repository.storeOne(data.uid(), data);
                successCount++;
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to store user data (uuid=" + data.uid() + ").", e);
                errorCount++;
            }
        }

        plugin.getLogger().info("Saved " + successCount + " users with " + errorCount + " errors.");
    }

    private void autosave() {
        int ticks = plugin.getSettings().getUserAutosaveInterval() * 60 * 20;

        if (ticks > 0) {
            this.autosaveTask = Plugins.sync(plugin, () -> {
                // Copy cached data so that it can be stored asynchronously without race conditions.
                Collection<UserData> snapshot = memento();

                // We can now save the snapshot asynchronously.
                Plugins.async(plugin, () -> save(snapshot));

                // Remove (purge) offline players from the cache.
                purgeCache();
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

        plugin.getServer().getPluginManager().registerEvents(playerJoinListener, plugin);
    }

    /* ---- SERVICE ---- */

    @Override
    public void load() {
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
    public void unload() {
        if (repository == null) {
            // Service is already disabled.
            return;
        }

        this.autosaveTask.cancel();
        this.autosaveTask = null;

        PlayerJoinEvent.getHandlerList().unregister(playerJoinListener);
        this.playerJoinListener = null;

        this.save(memento());
        this.cache.clear();
        this.cache = null;
        this.repository = null;
    }
}
