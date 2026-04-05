package com.github.fabiogvdneto.kits.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.kit.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import java.util.Collection;

public class CommandKits extends CommandHandler<KitPlugin> {

    public CommandKits(KitPlugin plugin) {
        super(plugin);
    }

    private String kitPermission() {
        return plugin.getSettings().getKitPermission();
    }

    private String kitPermission(String kitName) {
        return plugin.getSettings().getKitPermission(kitName);
    }

    private boolean hasKitPermission(Permissible permissible, String kitName) {
        return permissible.hasPermission(kitPermission(kitName)) || permissible.hasPermission(kitPermission());
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));

            Collection<String> kits = plugin.getKits().getAll().stream().map(Kit::getName)
                    .filter(kitName -> hasKitPermission(sender, kitName))
                    .toList();

            plugin.getMessages().kitList(sender, kits);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        }
    }
}
