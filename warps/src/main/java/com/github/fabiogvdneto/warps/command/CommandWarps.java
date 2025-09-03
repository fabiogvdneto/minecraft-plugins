package com.github.fabiogvdneto.warps.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.warps.WarpsPlugin;
import com.github.fabiogvdneto.warps.warp.Place;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class CommandWarps extends CommandHandler<WarpsPlugin> {

    public CommandWarps(WarpsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));

            Collection<String> warps = plugin.getWarps().getAll().stream()
                    .map(Place::getName)
                    .filter(warpName -> sender.hasPermission(plugin.getSettings().getWarpPermission(warpName)))
                    .toList();

            plugin.getMessages().warpList(sender, warps);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        }
    }
}
