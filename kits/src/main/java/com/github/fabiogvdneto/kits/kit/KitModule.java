package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.common.Plugins;
import com.github.fabiogvdneto.common.module.PluginModule;
import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.exception.KitAlreadyExistsException;
import com.github.fabiogvdneto.kits.exception.KitNotFoundException;
import com.github.fabiogvdneto.kits.repository.GsonKitRepository;
import com.github.fabiogvdneto.kits.repository.KitRepository;
import com.github.fabiogvdneto.kits.repository.data.KitData;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Level;

public class KitModule implements PluginModule {

    private final KitPlugin plugin;

    private KitRepository repository;
    private final Map<String, Kit> cache = new HashMap<>();

    private BukkitTask autosaver;
    private final List<String> dirty = new LinkedList<>();

    public KitModule(KitPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    /* ---- SERVICE ---- */

    @Override
    public void load() {
        if (repository != null) {
            // Module is already enabled.
            return;
        }

        createRepository();
        loadKits();
    }

    @Override
    public void unload() {
        if (repository == null) {
            // Module is already disabled.
            return;
        }

        if (this.autosaver != null) {
            this.autosaver.cancel();
            this.autosaver = null;
        }

        this.saveKits(serialize());
        this.cache.clear();
        this.repository = null;
    }

    /* ---- MANAGER ---- */

    public Collection<Kit> getAll() {
        return cache.values();
    }

    public Kit get(String name) throws KitNotFoundException {
        Kit kit = cache.get(name.toLowerCase());

        if (kit == null)
            throw new KitNotFoundException(name);

        return kit;
    }

    public Kit create(String name) throws KitAlreadyExistsException {
        String key = name.toLowerCase();

        if (cache.get(key) != null)
            throw new KitAlreadyExistsException(name);

        Kit kit = new Kit(plugin, this, name);
        cache.put(key, kit);

        dirty(key);
        return kit;
    }

    public Kit delete(String name) throws KitNotFoundException {
        String key = name.toLowerCase();
        Kit removed = cache.remove(key);

        if (removed == null)
            throw new KitNotFoundException(name);

        dirty(key);
        return removed;
    }

    public boolean exists(String name) {
        return cache.get(name.toLowerCase()) != null;
    }

    /* ---- PERSISTENCE ---- */

    private void createRepository() {
        try {
            this.repository = new GsonKitRepository(plugin);
            this.repository.create();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while trying create the kit repository.", e);
        }
    }

    /**
     * Mark this kit as dirty so that it can be saved on the next batch.
     * @param kit the kit that was modified
     */
    protected void dirty(String kit) {
        dirty.add(kit.toLowerCase());

        if (this.autosaver == null) {
            // Wait 10 minutes before saving.
            this.autosaver = Plugins.sync(plugin, () -> {
                this.autosaver = null;
                Map<String, KitData> data = serialize();
                Plugins.async(plugin, () -> saveKits(data));
            }, /* minutes */ 10 * /* seconds */ 60 * /* ticks */ 20);
        }
    }

    /**
     * Collect all data marked as dirty.
     * @return current snapshot of all kits marked as dirty
     */
    private Map<String, KitData> serialize() {
        Map<String, KitData> data = new HashMap<>();

        for (String key : dirty) {
            Kit kit = cache.get(key);

            // Null values represent data that was removed and must be deleted from the repository.
            data.put(key, (kit == null) ? null : kit.memento());
        }

        dirty.clear();
        return data;
    }

    private void loadKits() {
        try {
            for (KitData data : repository.fetchAll()) {
                cache.put(data.name().toLowerCase(), new Kit(plugin, this, data));
            }

            plugin.getLogger().info("Loaded " + cache.size() + " kits.");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to load kits.", e);
        }
    }

    private void saveKits(Map<String, KitData> data) {
        int successCount = 0;
        int errorCount = 0;

        for (Map.Entry<String, KitData> entry : data.entrySet()) {
            try {
                repository.storeOne(entry.getKey(), entry.getValue());
                successCount++;
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to save a kit.", e);
                errorCount++;
            }
        }

        plugin.getLogger().info("Saved " + successCount + " kits with " + errorCount + " errors.");
    }
}
