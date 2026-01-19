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

    public String getCooldownBypassPermission() {
        return "kits.bypass.cooldown";
    }

    public String getPriceBypassPermission() {
        return "kits.bypass.price";
    }

    public String getCommandPermission(Command cmd) {
        return "kits.command." + cmd.getName().toLowerCase();
    }

    public String getCommandPermission(String cmd) {
        return "kits.command." + cmd.toLowerCase();
    }

    public String getKitPermission(String kitName) {
        return "kits.redeem." + kitName.toLowerCase();
    }
}
