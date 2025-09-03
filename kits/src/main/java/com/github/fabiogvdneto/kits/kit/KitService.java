package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.common.PluginService;
import com.github.fabiogvdneto.common.Plugins;
import com.github.fabiogvdneto.kits.KitsPlugin;
import com.github.fabiogvdneto.kits.exception.KitAlreadyExistsException;
import com.github.fabiogvdneto.kits.exception.KitNotFoundException;
import com.github.fabiogvdneto.kits.repository.KitRepository;
import com.github.fabiogvdneto.kits.repository.data.KitData;
import com.github.fabiogvdneto.kits.repository.java.JavaKitRepository;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class KitService implements KitManager, PluginService {

    private final KitsPlugin plugin;
    private final Map<String, Kit> cache = new HashMap<>();

    private KitRepository repository;
    private BukkitTask autosaveTask;

    public KitService(KitsPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public Collection<Kit> getAll() {
        return cache.values().stream().filter(Objects::nonNull).toList();
    }

    @Override
    public Kit get(String name) throws KitNotFoundException {
        Kit kit = cache.get(getKeyFromName(name));

        if (kit == null)
            throw new KitNotFoundException(name);

        return kit;
    }

    @Override
    public Kit create(String name) throws KitAlreadyExistsException {
        String key = getKeyFromName(name);

        if (cache.get(key) != null)
            throw new KitAlreadyExistsException(name);

        Kit kit = new SimpleKit(name);
        cache.put(key, kit);

        dirty();
        return kit;
    }

    @Override
    public Kit delete(String name) throws KitNotFoundException {
        // Replace the value by null instead of removing it,
        // so that it can be deleted from the repository later.
        String key = getKeyFromName(name);
        Kit removed = cache.replace(key, null);

        if (removed == null)
            throw new KitNotFoundException(name);

        dirty();
        return removed;
    }

    @Override
    public boolean exists(String name) {
        return cache.get(getKeyFromName(name)) != null;
    }

    @Override
    public void enable() {
        disable();
        createRepository();
        loadKits();
    }

    private void createRepository() {
        this.repository = new JavaKitRepository(plugin.getDataPath().resolve("kits"));

        try {
            repository.create();
        } catch (Exception e) {
            plugin.getLogger().warning("Could not create the kits repository.");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    @Override
    public void disable() {
        if (repository == null) return;

        if (autosaveTask != null) {
            autosaveTask.cancel();
        }

        save(memento());
        cache.clear();

        this.autosaveTask = null;
        this.repository = null;
    }

    /* ---- Persistence ---- */

    private void loadKits() {
        try {
            for (KitData data : repository.fetchAll()) {
                cache.put(getKey(data), new SimpleKit(data));
            }
            plugin.getLogger().info("Loaded " + cache.size() + " kits.");
        } catch (Exception e) {
            plugin.getLogger().warning("An error occurred while trying to load kits.");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    private void save(Map<String, KitData> data) {
        int successCount = 0;
        int errorCount = 0;

        for (Map.Entry<String, KitData> entry : data.entrySet()) {
            if (entry.getValue() == null) {
                // Delete from the repository.
                try {
                    repository.deleteOne(entry.getKey());
                    successCount++;
                } catch (Exception e) {
                    plugin.getLogger().warning("Could not delete a kit.");
                    plugin.getLogger().warning(e.getMessage());
                    errorCount++;
                }
            } else {
                // Store to the repository.
                try {
                    repository.storeOne(entry.getValue());
                    successCount++;
                } catch (Exception e) {
                    plugin.getLogger().warning("Could not delete a kit.");
                    plugin.getLogger().warning(e.getMessage());
                    errorCount++;
                }
            }
        }

        plugin.getLogger().info("Modified " + successCount + " kits with " + errorCount + " errors.");
    }

    @Override
    public void dirty() {
        if (autosaveTask != null)
            return;

        // Wait 10 minutes before saving.
        this.autosaveTask = Plugins.sync(plugin, () -> {
            this.autosaveTask = null;
            Map<String, KitData> data = memento();
            Plugins.async(plugin, () -> save(data));
        }, 10 * 60 * 20);
    }

    /* ---- Utilities ---- */

    private String getKeyFromName(String name) {
        return name.toLowerCase();
    }

    private String getKey(KitData data) {
        return data.name().toLowerCase();
    }

    private Map<String, KitData> memento() {
        Map<String, KitData> data = new HashMap<>();

        Iterator<Map.Entry<String, Kit>> it = cache.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Kit> entry = it.next();

            if (entry.getValue() == null) {
                // Null values represent data that was removed and must be deleted from the repository.
                data.put(entry.getKey(), null);
                it.remove();
            } else {
                // The remaining values must be stored in the repository.
                data.put(entry.getKey(), ((SimpleKit) entry.getValue()).memento());
            }
        }

        return data;
    }
}
