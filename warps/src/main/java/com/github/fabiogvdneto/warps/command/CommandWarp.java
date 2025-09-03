package com.github.fabiogvdneto.warps.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.CommandSenderException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.warps.WarpsPlugin;
import com.github.fabiogvdneto.warps.exception.WarpNotFoundException;
import com.github.fabiogvdneto.warps.warp.Place;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class CommandWarp extends CommandHandler<WarpsPlugin> {

    public CommandWarp(WarpsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));
            requireArguments(args, 1);

            Place warp = plugin.getWarps().get(args[0]);

            requirePermission(sender, plugin.getSettings().getWarpPermission(warp.getName()));

            plugin.teleport((Player) sender, warp);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (CommandArgumentException e) {
            plugin.getServer().dispatchCommand(sender, "warps");
        } catch (WarpNotFoundException e) {
            plugin.getMessages().warpNotFound(sender);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        Stream<String> warps = plugin.getWarps().getAll().stream()
                .filter(warp -> !warp.isClosed())
                .map(Place::getName)
                .filter(warpName -> warpName.startsWith(args[0]));

        return sender.hasPermission(plugin.getSettings().getAdminPermission())
                ? warps.toList()
                : warps.filter(warpName -> sender.hasPermission(plugin.getSettings().getWarpPermission(warpName))).toList();
    }
}
