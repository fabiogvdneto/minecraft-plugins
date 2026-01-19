package com.github.fabiogvdneto.cursedwarps.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.cursedwarps.WarpPlugin;
import com.github.fabiogvdneto.cursedwarps.warp.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class CommandWarps extends CommandHandler<WarpPlugin> {

    public CommandWarps(WarpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));

            Collection<String> warps = plugin.getWarps().getAll().stream()
                    .map(Warp::getName)
                    .filter(warpName -> sender.hasPermission(plugin.getSettings().getWarpPermission(warpName)))
                    .toList();

            plugin.getMessages().warpList(sender, warps);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        }
    }
}
