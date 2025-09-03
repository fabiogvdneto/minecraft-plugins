package com.github.fabiogvdneto.kits;

import com.github.fabiogvdneto.common.service.ConfigurationServiceBase;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;

public class ConfigurationService extends ConfigurationServiceBase {

    public ConfigurationService(Plugin plugin) {
        super(plugin);
    }

    public String getLanguage() {
        return config().getString("lang");
    }

    public int getKitAutosaveMinutes() {
        return config().getInt("kits.autosave-minutes");
    }

    /* ---- Permissions ---- */

    public String getCommandPermission(Command cmd) {
        return "kits.command." + cmd.getName().toLowerCase();
    }

    public String getCommandPermission(String cmd) {
        return "kits.command." + cmd.toLowerCase();
    }

    public String getKitPermission(String kitName) {
        return "kits.kit." + kitName.toLowerCase();
    }
}
