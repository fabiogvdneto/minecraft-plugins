package com.github.fabiogvdneto.warps.user;

import com.github.fabiogvdneto.common.PluginService;
import com.github.fabiogvdneto.common.Plugins;
import com.github.fabiogvdneto.warps.WarpsPlugin;
import com.github.fabiogvdneto.warps.repository.UserRepository;
import com.github.fabiogvdneto.warps.repository.data.UserData;
import com.github.fabiogvdneto.warps.repository.gson.GsonUserRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UserService implements UserManager, PluginService {

    private final WarpsPlugin plugin;
    private final Map<UUID, CompletableFuture<User>> cache = new HashMap<>();

    private UserRepository repository;
    private BukkitTask autosaveTask;
    private Listener playerListener;

    public UserService(WarpsPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public Collection<User> getAll() {
        return cache.values().stream().map(future -> future.getNow(null)).filter(Objects::nonNull).toList();
    }

    @Override
    public User getIfCached(UUID userId) {
        CompletableFuture<User> future = cache.get(userId);
        return (future == null) ? null : future.getNow(null);
    }

    @Override
    public CompletableFuture<User> fetch(UUID userId) {
        return cache.computeIfAbsent(userId, key -> {
            CompletableFuture<User> future = new CompletableFuture<>();

            Plugins.async(plugin, () -> {
                try {
                    UserData data = repository.fetchOne(userId.toString());
                    SimpleUser user = (data == null) ? new SimpleUser(userId) : new SimpleUser(data);
                    future.complete(user);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });

            return future.exceptionally(e -> {
                plugin.getLogger().warning("Could not load user data (uuid: " + userId + ").");
                plugin.getLogger().warning(e.getMessage());
                cache.remove(userId);
                return null;
            });
        });
    }

    @Override
    public void enable() {
        disable();
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
        this.repository = new GsonUserRepository(plugin.getDataPath().resolve("data").resolve("users"));

        try {
            repository.create();
        } catch (Exception e) {
            plugin.getLogger().warning("Could not create the user repository.");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    @Override
    public void disable() {
        if (repository == null) return;

        PlayerJoinEvent.getHandlerList().unregister(playerListener);
        autosaveTask.cancel();
        save(memento());
        cache.clear();

        this.playerListener = null;
        this.autosaveTask = null;
        this.repository = null;
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
                repository.storeOne(data.uid().toString(), data);
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
        try {
            plugin.getLogger().info("Purged " + repository.purge(purgeDays) + " users.");
        } catch (Exception e) {
            plugin.getLogger().warning("Could not purge user data.");
            plugin.getLogger().warning(e.getMessage());
        }
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
        return cache.values().stream()
                .map(future -> future.getNow(null))
                .filter(Objects::nonNull)
                .map(user -> ((SimpleUser) user).memento())
                .toList();
    }

    private void refreshCache() {
        Set<UUID> onlinePlayers = plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toUnmodifiableSet());

        cache.keySet().retainAll(onlinePlayers);
    }
}
