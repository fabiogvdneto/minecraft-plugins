package com.github.fabiogvdneto.warps.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.warps.WarpPlugin;
import com.github.fabiogvdneto.warps.warp.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class CommandWarps extends CommandHandler<WarpPlugin> {

    public CommandWarps(WarpPlugin plugin) {
        super(plugin);
    }

    private String warpPermission(String warpName) {
        return plugin.getSettings().getWarpPermission(warpName);
    }

    private boolean hasPermission(CommandSender sender, Warp warp) {
        return !warp.isClosed() || sender.hasPermission(warpPermission(warp.getName()));
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));

            Collection<String> warps = plugin.getWarps().getAll().stream()
                    .filter(warp -> hasPermission(sender, warp))
                    .map(Warp::getName)
                    .toList();

            plugin.getMessages().warpList(sender, warps);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        }
    }
}
