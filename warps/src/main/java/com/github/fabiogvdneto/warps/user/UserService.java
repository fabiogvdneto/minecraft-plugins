package com.github.fabiogvdneto.warps.user;

import com.github.fabiogvdneto.common.PluginService;
import com.github.fabiogvdneto.common.Plugins;
import com.github.fabiogvdneto.common.repository.CacheManager;
import com.github.fabiogvdneto.warps.WarpsPlugin;
import com.github.fabiogvdneto.warps.repository.data.UserData;
import com.github.fabiogvdneto.warps.repository.gson.GsonUserRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UserService implements UserManager, PluginService {

    private final WarpsPlugin plugin;
    private Listener playerListener;

    private CacheManager<UUID, UserData, User> cache;
    private BukkitTask autosaveTask;

    public UserService(WarpsPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

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
        return cache.fetch(userId);
    }

    @Override
    public void enable() {
        if (cache != null) {
            // Service is already enabled.
            return;
        }

        createRepository();
        registerEvents();
        loadOnlinePlayers();
        runAutosave();
    }

    private void registerEvents() {
        this.playerListener = new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                fetch(event.getPlayer().getUniqueId());
            }
        };

        plugin.getServer().getPluginManager().registerEvents(playerListener, plugin);
    }

    private void createRepository() {
        Path path = plugin.getDataPath().resolve("data").resolve("users");
        GsonUserRepository repository = new GsonUserRepository(path);

        try {
            repository.create();

            this.cache = new CacheManager<>(plugin, repository) {
                @Override
                protected User parse(UUID uid, @Nullable UserData data) {
                    return (data == null) ? new SimpleUser(uid) : new SimpleUser(data);
                }
            };
        } catch (Exception e) {
            plugin.getLogger().warning("Could not create the user repository.");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    @Override
    public void disable() {
        if (cache == null) {
            // Service is already disabled.
            return;
        }

        PlayerJoinEvent.getHandlerList().unregister(playerListener);
        autosaveTask.cancel();
        save(memento());
        cache.clear();

        this.playerListener = null;
        this.autosaveTask = null;
        this.cache = null;
    }

    // ---- Persistence ----

    private void loadOnlinePlayers() {
        Player[] snapshot = Bukkit.getOnlinePlayers().toArray(Player[]::new);

        for (Player player : snapshot) {
            fetch(player.getUniqueId());
        }
    }

    private void save(Collection<UserData> snapshot) {
        int successCount = 0;
        int errorCount = 0;

        for (UserData data : snapshot) {
            try {
                cache.getRepository().storeOne(data.uid(), data);
                successCount++;
            } catch (Exception e) {
                plugin.getLogger().warning("Could not save user data (uuid: " + data.uid() + ").");
                plugin.getLogger().warning(e.getMessage());
                errorCount++;
            }
        }

        plugin.getLogger().info("Saved " + successCount + " users with " + errorCount + " errors.");
    }

    private void purge(int purgeDays) {
        // TODO
    }

    private void runAutosave() {
        int ticks = plugin.getSettings().getUserAutosaveInterval() * 60 * 20;
        int purgeDays = plugin.getSettings().getUserPurgeDays();

        this.autosaveTask = Plugins.sync(plugin, () -> {
            // Copy cached data so that it can be stored asynchronously without race conditions.
            Collection<UserData> snapshot = memento();

            Plugins.async(plugin, () -> {
                // Save cache to the repository.
                save(snapshot);
                // Remove (purge) old data from the repository.
                purge(purgeDays);
            });

            // Remove (purge) offline players from the cache.
            refreshCache();
        }, ticks, ticks);
    }

    // ---- Utilities ----

    private List<UserData> memento() {
        return cache.getAll().stream()
                .map(user -> ((SimpleUser) user).memento())
                .toList();
    }

    private void refreshCache() {
        Set<UUID> onlinePlayers = plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toUnmodifiableSet());

        cache.getKeys().retainAll(onlinePlayers);
    }
}
