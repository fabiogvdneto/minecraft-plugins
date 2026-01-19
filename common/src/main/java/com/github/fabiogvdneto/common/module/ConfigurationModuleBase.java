package com.github.fabiogvdneto.common.module;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

public abstract class ConfigurationModuleBase implements PluginModule {

    protected final Plugin plugin;

    public ConfigurationModuleBase(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        // Reload config from disk.
        plugin.reloadConfig();
        // Save raw configuration (preserve all comments).
        // Not needed in future versions of spigot/paper (1.18+).
        plugin.saveDefaultConfig();
    }

    @Override
    public void unload() {
        // Nothing to do.
    }

    protected Configuration config() {
        return plugin.getConfig();
    }
}
