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

public class WarpService implements WarpManager, PluginService {

    private final WarpsPlugin plugin;
    private final Map<String, Place> cache = new HashMap<>();

    private BukkitTask autosaveTask;
    private WarpRepository repository;

    public WarpService(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

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

    @Override
    public void enable() {
        disable();
        createRepository();
        autosave();
    }

    private void createRepository() {
        this.repository = new GsonWarpRepository(plugin.getDataPath().resolve("data").resolve("warps.json"));

        try {
            repository.create();
            repository.fetch().forEach(data -> cache.put(data.name().toLowerCase(), new SimpleWarp(data)));
            plugin.getLogger().info("Loaded " + cache.size() + " warps.");
        } catch (Exception e) {
            plugin.getLogger().warning("Could not load warp data.");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    private void autosave() {
        int ticks = plugin.getSettings().getWarpAutosaveInterval() * 60 * 20;

        if (ticks > 0) {
            // 36.000 ticks = 30 minutes
            this.autosaveTask = Plugins.sync(plugin, () -> {
                Collection<WarpData> data = memento();
                Plugins.async(plugin, () -> save(data));
            }, ticks, ticks);
        }
    }

    @Override
    public void disable() {
        if (repository == null)  return;

        autosaveTask.cancel();
        save(memento());

        this.autosaveTask = null;
        this.repository = null;
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

    private Collection<WarpData> memento() {
        return cache.values().stream().map(warp -> ((SimpleWarp) warp).memento()).toList();
    }

    public WarpRepository getRepository() {
        return repository;
    }
}
