package com.github.fabiogvdneto.common;

import com.github.fabiogvdneto.common.menu.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Plugins {

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

    public static YamlConfiguration loadResource(Plugin plugin, String path) {
        InputStream resource = plugin.getResource(path);

        if (resource == null)
            return new YamlConfiguration();

        return YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
    }

    /**
     * Loads a yaml configuration file from the data folder.
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
    public static YamlConfiguration createConfigurationFromResource(Plugin plugin, String path) {
        File file = new File(plugin.getDataFolder(), path);

        YamlConfiguration defaults = loadResource(plugin, path);
        YamlConfiguration config = file.isFile() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();

        plugin.saveResource(path, /* replace */ false);
        config.setDefaults(defaults);
        return config;
    }

    public static void createInventory(Plugin plugin, Player player) {
        new Menu(9, Component.text("title"))
                .withItem(5, ItemStack.of(Material.ACACIA_BOAT))
                .withAction(5, event -> event.getWhoClicked().recalculatePermissions())
                .open(player);
    }
}
