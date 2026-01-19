package com.github.fabiogvdneto.cursedwarps.warp;

import com.github.fabiogvdneto.common.Plugins;
import com.github.fabiogvdneto.common.module.PluginModule;
import com.github.fabiogvdneto.cursedwarps.WarpPlugin;
import com.github.fabiogvdneto.cursedwarps.exception.WarpAlreadyExistsException;
import com.github.fabiogvdneto.cursedwarps.exception.WarpNotFoundException;
import com.github.fabiogvdneto.cursedwarps.repository.WarpRepository;
import com.github.fabiogvdneto.cursedwarps.repository.data.WarpData;
import com.github.fabiogvdneto.cursedwarps.repository.gson.GsonWarpRepository;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class WarpModule implements WarpService, PluginModule {

    private final WarpPlugin plugin;

    private final Map<String, Warp> cache = new HashMap<>();
    private WarpRepository repository;

    public WarpModule(WarpPlugin plugin) {
        this.plugin = plugin;
    }

    /* ---- CACHE ---- */

    @Override
    public Collection<Warp> getAll() {
        return cache.values();
    }

    @Override
    public Warp get(String name) throws WarpNotFoundException {
        Warp warp = cache.get(name.toLowerCase());

        if (warp == null)
            throw new WarpNotFoundException();

        return warp;
    }

    @Override
    public Warp create(String name, Location location) throws WarpAlreadyExistsException {
        Warp warp = new WarpImpl(name, location);

        if (cache.putIfAbsent(name.toLowerCase(), warp) != null)
            throw new WarpAlreadyExistsException();

        saveAsync();
        return warp;
    }

    @Override
    public void delete(String name) throws WarpNotFoundException {
        if (cache.remove(name.toLowerCase()) == null)
            throw new WarpNotFoundException();

        saveAsync();
    }

    private Collection<WarpData> memento() {
        return cache.values().stream().map(warp -> ((WarpImpl) warp).memento()).toList();
    }

    /* ---- PERSISTENCE ---- */

    private void saveAsync() {
        Collection<WarpData> data = memento();
        Plugins.async(plugin, () -> save(data));
    }

    private void save(Collection<WarpData> data) {
        try {
            repository.store(data);
            plugin.getLogger().info("Saved " + data.size() + " warps.");
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save warps.");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    /* ---- SERVICE ---- */

    @Override
    public void load() {
        if (repository != null) {
            // Service is already enabled.
            return;
        }

        try {
            this.repository = new GsonWarpRepository(plugin.getDataPath().resolve("warps.json"));
            this.repository.create();
            this.repository.fetch().forEach(data -> cache.put(data.name().toLowerCase(), new WarpImpl(data)));
            this.plugin.getLogger().info("Loaded " + cache.size() + " warps.");
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to load warp data.", e);
        }
    }

    @Override
    public void unload() {
        if (repository == null) {
            // Service is already disabled.
            return;
        }

        this.save(memento());
        this.repository = null;
    }
}