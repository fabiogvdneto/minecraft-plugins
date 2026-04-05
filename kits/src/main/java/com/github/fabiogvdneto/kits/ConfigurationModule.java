package com.github.fabiogvdneto.kits;

import com.github.fabiogvdneto.common.module.ConfigurationModuleBase;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;

public class ConfigurationModule extends ConfigurationModuleBase {

    public ConfigurationModule(Plugin plugin) {
        super(plugin);
    }

    public String getLanguage() {
        return config().getString("lang");
    }

    /* ---- Permissions ---- */

    public String getAdminPermission() {
        return "kits.admin";
    }

    public String getCommandPermission(Command cmd) {
        return "kits.command." + cmd.getName().toLowerCase();
    }

    public String getCommandPermission(String cmd) {
        return "kits.command." + cmd.toLowerCase();
    }

    public String getKitPermission() {
        return "kits.kit.*";
    }

    public String getKitPermission(String kitName) {
        return "kits.kit." + kitName.toLowerCase();
    }
}
