package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.common.PluginService;
import com.github.fabiogvdneto.common.Plugins;
import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.exception.KitAlreadyExistsException;
import com.github.fabiogvdneto.kits.exception.KitNotFoundException;
import com.github.fabiogvdneto.kits.repository.GsonKitRepository;
import com.github.fabiogvdneto.kits.repository.KitRepository;
import com.github.fabiogvdneto.kits.repository.data.KitData;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class KitService implements KitManager, PluginService {

    private final KitPlugin plugin;
    private KitRepository repository;

    private BukkitTask autosaveTask;

    private final Map<String, Kit> cache = new HashMap<>();
    private final List<String> dirty = new LinkedList<>();

    public KitService(KitPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

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

        Kit kit = new SimpleKit(this.plugin, this, name);
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

    @Override
    public void enable() {
        disable();
        createRepository();
        loadKits();
    }

    @Override
    public void disable() {
        if (this.repository == null) return;

        if (this.autosaveTask != null) {
            this.autosaveTask.cancel();
            this.autosaveTask = null;
        }

        saveKits(dirty());

        this.cache.clear();
        this.repository = null;
    }

    /* ---- Persistence ---- */

    private void createRepository() {
        try {
            this.repository = new GsonKitRepository(this.plugin.getDataPath().resolve("kits"));
            this.repository.create();
        } catch (Exception e) {
            this.plugin.getLogger().warning("Could not create the kits repository.");
            this.plugin.getLogger().warning(e.getMessage());
        }
    }

    private void loadKits() {
        try {
            for (KitData data : this.repository.fetchAll()) {
                this.cache.put(key(data), new SimpleKit(this.plugin, this, data));
            }

            this.plugin.getLogger().info("Loaded " + this.cache.size() + " kits.");
        } catch (Exception e) {
            this.plugin.getLogger().warning("An error occurred while trying to load kits.");
            this.plugin.getLogger().warning(e.getMessage());
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
                this.plugin.getLogger().warning("Could not store a kit.");
                this.plugin.getLogger().warning(e.getMessage());
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
                data.put(key, ((SimpleKit) kit).memento());
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

        if (this.autosaveTask == null) {
            // Wait 10 minutes before saving.
            this.autosaveTask = Plugins.sync(this.plugin, () -> {
                this.autosaveTask = null;
                Map<String, KitData> data = dirty();
                Plugins.async(this.plugin, () -> saveKits(data));
            }, 10 * 60 * 20);
        }
    }
}
