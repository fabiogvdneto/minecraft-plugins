package com.github.fabiogvdneto.kits;

import com.github.fabiogvdneto.common.module.ConfigurationModuleBase;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;

public class ConfigurationModule extends ConfigurationModuleBase {

    private final static String PERMISSION_PREFIX = "morekits.";

    public ConfigurationModule(Plugin plugin) {
        super(plugin);
    }

    /* ---- Configuration ---- */

    public String getLanguage() {
        return config().getString("lang");
    }

    /* ---- Permissions ---- */

    public String getAdminPermission() {
        return PERMISSION_PREFIX + "admin";
    }

    public String getCommandPermission(Command cmd) {
        return PERMISSION_PREFIX + "command." + cmd.getName().toLowerCase();
    }

    public String getCommandPermission(String cmd) {
        return PERMISSION_PREFIX + "command." + cmd.toLowerCase();
    }

    public String getKitPermission() {
        return PERMISSION_PREFIX + "kit.*";
    }

    public String getKitPermission(String kitName) {
        return PERMISSION_PREFIX + "kit." + kitName.toLowerCase();
    }
}
