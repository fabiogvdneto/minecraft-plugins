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

    /* ---- Permissions ---- */

    public String getCooldownBypassPermission() {
        return "wasdkits.bypass.cooldown";
    }

    public String getPriceBypassPermission() {
        return "wasdkits.bypass.price";
    }

    public String getCommandPermission(Command cmd) {
        return "wasdkits.command." + cmd.getName().toLowerCase();
    }

    public String getCommandPermission(String cmd) {
        return "wasdkits.command." + cmd.toLowerCase();
    }

    public String getKitPermission(String kitName) {
        return "wasdkits.redeem." + kitName.toLowerCase();
    }
}
