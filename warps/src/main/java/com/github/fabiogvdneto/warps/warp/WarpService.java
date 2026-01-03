package com.github.fabiogvdneto.warps.warp;

import com.github.fabiogvdneto.common.PluginService;
import com.github.fabiogvdneto.common.Plugins;
import com.github.fabiogvdneto.warps.WarpsPlugin;
import com.github.fabiogvdneto.warps.exception.WarpAlreadyExistsException;
import com.github.fabiogvdneto.warps.exception.WarpNotFoundException;
import com.github.fabiogvdneto.warps.repository.WarpRepository;
import com.github.fabiogvdneto.warps.repository.data.WarpData;
import com.github.fabiogvdneto.warps.repository.gson.GsonWarpRepository;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class WarpService implements WarpManager, PluginService {

    private final WarpsPlugin plugin;

    private final Map<String, Place> cache = new HashMap<>();
    private BukkitTask autosaveTask;
    private WarpRepository repository;

    public WarpService(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    /* ---- CACHE ---- */

    @Override
    public Collection<Place> getAll() {
        return cache.values();
    }

    @Override
    public Place get(String name) throws WarpNotFoundException {
        Place place = cache.get(name.toLowerCase());

        if (place == null)
            throw new WarpNotFoundException();

        return place;
    }

    @Override
    public Place create(String name, Location location) throws WarpAlreadyExistsException {
        Place warp = new SimpleWarp(name, location);

        if (cache.putIfAbsent(name.toLowerCase(), warp) != null)
            throw new WarpAlreadyExistsException();

        return warp;
    }

    @Override
    public void delete(String name) throws WarpNotFoundException {
        if (cache.remove(name.toLowerCase()) == null)
            throw new WarpNotFoundException();
    }

    private Collection<WarpData> memento() {
        return cache.values().stream().map(warp -> ((SimpleWarp) warp).memento()).toList();
    }

    /* ---- PERSISTENCE ---- */

    private void createRepository() {
        try {
            this.repository = new GsonWarpRepository(plugin.getDataPath().resolve("warps.json"));
            this.repository.create();
            this.repository.fetch().forEach(data -> cache.put(data.name().toLowerCase(), new SimpleWarp(data)));
            this.plugin.getLogger().info("Loaded " + cache.size() + " warps.");
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to load warp data.", e);
        }
    }

    private void autosave() {
        int ticks = plugin.getSettings().getWarpAutosaveInterval() * 60 * 20;

        if (ticks > 0) {
            this.autosaveTask = Plugins.sync(plugin, () -> {
                Collection<WarpData> data = memento();
                Plugins.async(plugin, () -> save(data));
            }, ticks, ticks);
        }
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
    public void enable() {
        if (repository != null) {
            // Service is already enabled.
            return;
        }

        this.createRepository();
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

        this.save(memento());
        this.repository = null;
    }
}