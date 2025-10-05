package com.github.fabiogvdneto.common.service;

import com.github.fabiogvdneto.common.PluginService;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

public abstract class ConfigurationServiceBase implements PluginService {

    protected final Plugin plugin;

    public ConfigurationServiceBase(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        // Reload config from disk.
        plugin.reloadConfig();
        // Save raw configuration (preserve all comments).
        // Not needed in future versions of spigot/paper (1.18+).
        plugin.saveDefaultConfig();
    }

    @Override
    public void disable() {
        // Nothing to do.
    }

    protected Configuration config() {
        return plugin.getConfig();
    }
}
