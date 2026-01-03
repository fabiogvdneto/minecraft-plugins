package com.github.fabiogvdneto.common.repository;

import com.github.fabiogvdneto.common.Plugins;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class PluginCache<K, V> {

    private final Plugin plugin;
    private final Map<K, CompletableFuture<V>> cache;

    public PluginCache(Plugin plugin) {
        this.plugin = plugin;
        this.cache = new HashMap<>();
    }

    public Collection<K> getKeys() {
        return cache.keySet();
    }

    public Collection<V> getAll() {
        return cache.values().stream().map(future -> future.getNow(null)).filter(Objects::nonNull).toList();
    }

    public V get(K id) {
        CompletableFuture<V> future = cache.get(id);
        return (future == null) ? null : future.getNow(null);
    }

    public CompletableFuture<V> fetch(K id, Callable<? extends V> loader) {
        return cache.computeIfAbsent(id, key -> {
            CompletableFuture<V> future = new CompletableFuture<>();

            Plugins.async(plugin, () -> {
                try {
                    future.complete(loader.call());
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });

            return future.exceptionally(e -> {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to load data.", e);
                cache.remove(id);
                return null;
            });
        });
    }

    public void clear() {
        cache.clear();
    }
}
