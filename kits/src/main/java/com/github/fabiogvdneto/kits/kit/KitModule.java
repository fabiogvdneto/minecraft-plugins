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

public class KitModule implements KitService, PluginModule {

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
        if (this.repository != null) {
            // Module is already enabled.
            return;
        }

        createRepository();
        loadKits();
    }

    @Override
    public void unload() {
        if (this.repository == null) {
            // Module is already disabled.
            return;
        }

        if (this.autosaver != null) {
            this.autosaver.cancel();
            this.autosaver = null;
        }

        saveKits(dirty());

        this.cache.clear();
        this.repository = null;
    }

    /* ---- MANAGER ---- */

    private String key(String name) {
        return name.toLowerCase();
    }

    private String key(KitData data) {
        return data.name().toLowerCase();
    }

    @Override
    public Collection<Kit> getAll() {
        return this.cache.values();
    }

    @Override
    public Kit get(String name) throws KitNotFoundException {
        Kit kit = this.cache.get(key(name));

        if (kit == null)
            throw new KitNotFoundException(name);

        return kit;
    }

    @Override
    public Kit create(String name) throws KitAlreadyExistsException {
        String key = key(name);

        if (this.cache.get(key) != null)
            throw new KitAlreadyExistsException(name);

        Kit kit = new KitImpl(this.plugin, this, name);
        this.cache.put(key, kit);

        dirty(key);
        return kit;
    }

    @Override
    public Kit delete(String name) throws KitNotFoundException {
        // Replace the value by null instead of removing it,
        // so that it can be deleted from the repository later.
        String key = key(name);
        Kit removed = this.cache.replace(key, null);

        if (removed == null)
            throw new KitNotFoundException(name);

        dirty(key);
        return removed;
    }

    @Override
    public boolean exists(String name) {
        return this.cache.get(key(name)) != null;
    }

    /* ---- PERSISTENCE ---- */

    private void createRepository() {
        try {
            this.repository = new GsonKitRepository(this.plugin.getDataPath().resolve("kits"));
            this.repository.create();
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while trying create the kit repository.", e);
        }
    }

    private void loadKits() {
        try {
            for (KitData data : this.repository.fetchAll()) {
                this.cache.put(key(data), new KitImpl(this.plugin, this, data));
            }

            this.plugin.getLogger().info("Loaded " + this.cache.size() + " kits.");
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to load kits.", e);
        }
    }

    private void saveKits(Map<String, KitData> data) {
        int successCount = 0;
        int errorCount = 0;

        for (Map.Entry<String, KitData> entry : data.entrySet()) {
            try {
                this.repository.storeOne(entry.getKey(), entry.getValue());
                successCount++;
            } catch (Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to save a kit.", e);
                errorCount++;
            }
        }

        this.plugin.getLogger().info("Saved " + successCount + " kits with " + errorCount + " errors.");
    }

    /**
     * Collect all data marked as dirty.
     * @return current snapshot of all kits marked as dirty
     */
    private Map<String, KitData> dirty() {
        Map<String, KitData> data = new HashMap<>();

        for (String key : this.dirty) {
            Kit kit = this.cache.get(key);

            if (kit == null) {
                // Null values represent data that was removed and must be deleted from the repository.
                data.put(key, null);
            } else {
                // The remaining values must be stored in the repository.
                data.put(key, ((KitImpl) kit).memento());
            }
        }

        this.dirty.clear();
        return data;
    }

    /**
     * Mark this kit as dirty so that it can be saved on the next batch.
     * @param kit the kit that was modified
     */
    void dirty(String kit) {
        this.dirty.add(key(kit));

        if (this.autosaver == null) {
            // Wait 10 minutes before saving.
            this.autosaver = Plugins.sync(this.plugin, () -> {
                this.autosaver = null;
                Map<String, KitData> data = dirty();
                Plugins.async(this.plugin, () -> saveKits(data));
            }, 10 * 60 * 20);
        }
    }
}
