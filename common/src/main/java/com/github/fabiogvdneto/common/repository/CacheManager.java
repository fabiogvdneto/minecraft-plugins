package com.github.fabiogvdneto.common.repository;

import com.github.fabiogvdneto.common.Plugins;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public abstract class CacheManager<K, D, V> {

    private final Plugin plugin;
    private final KeyedRepository<K, D> repository;
    private final Map<K, CompletableFuture<V>> cache;

    public CacheManager(Plugin plugin, KeyedRepository<K, D> repository) {
        this.plugin = plugin;
        this.repository = Objects.requireNonNull(repository);
        this.cache = new HashMap<>();
    }

    public KeyedRepository<K, D> getRepository() {
        return repository;
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

    public CompletableFuture<V> fetch(K id) {
        return cache.computeIfAbsent(id, key -> {
            CompletableFuture<V> future = new CompletableFuture<>();

            Plugins.async(plugin, () -> {
                try {
                    D data = repository.fetchOne(key);
                    future.complete(parse(key, data));
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

    protected abstract V parse(K key, @Nullable D data);
}
