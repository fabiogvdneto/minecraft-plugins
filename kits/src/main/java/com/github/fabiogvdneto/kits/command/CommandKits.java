package com.github.fabiogvdneto.kits.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.kit.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class CommandKits extends CommandHandler<KitPlugin> {

    public CommandKits(KitPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));

            Collection<String> kits = plugin.getKits().getAll().stream().map(Kit::getName)
                    .filter(kitName -> sender.hasPermission(plugin.getSettings().getKitPermission(kitName)))
                    .toList();

            if (kits.isEmpty()) {
                plugin.getMessages().kitListEmpty(sender);
                return;
            }

            plugin.getMessages().kitList(sender, kits);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        }
    }
}
