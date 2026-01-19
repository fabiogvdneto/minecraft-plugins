package com.github.fabiogvdneto.common;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Plugins {

    public static void registerEvents(Plugin plugin, Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public static BukkitTask async(Plugin plugin, Runnable task) {
        return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    public static BukkitTask async(Plugin plugin, Runnable task, long delay) {
        return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    public static BukkitTask async(Plugin plugin, Runnable task, long delay, long period) {
        return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    public static BukkitTask sync(Plugin plugin, Runnable task) {
        return plugin.getServer().getScheduler().runTask(plugin, task);
    }

    public static BukkitTask sync(Plugin plugin, Runnable task, long delay) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, task, delay);
    }

    public static BukkitTask sync(Plugin plugin, Runnable task, long delay, long period) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, task, delay, period);
    }

    /**
     * Loads a yaml configuration from the plugin maven/jar resources.
     */
    public static YamlConfiguration loadResource(Plugin plugin, String path) {
        InputStream resource = plugin.getResource(path);

        if (resource == null)
            return new YamlConfiguration();

        return YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
    }

    /**
     * Loads a yaml configuration from the plugin data folder.
     */
    public static YamlConfiguration loadConfiguration(Plugin plugin, String path) {
        File file = new File(plugin.getDataFolder(), path);

        return file.isFile() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
    }

    /**
     * Loads two yaml configurations: one from maven resources, and another one from the data folder.
     * The configuration from the data folder overrides the configuration from maven resources.
     * If the configuration in the data folder does not exist, it will be created by saving the resource.
     */
    public static YamlConfiguration loadConfigurationWithDefaults(Plugin plugin, String path) {
        YamlConfiguration config = loadConfiguration(plugin, path);
        YamlConfiguration defaults = loadResource(plugin, path);

        plugin.saveResource(path, /* replace */ false);
        config.setDefaults(defaults);
        return config;
    }
}
