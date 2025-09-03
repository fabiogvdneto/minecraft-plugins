package com.github.fabiogvdneto.warps.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.warps.WarpsPlugin;
import com.github.fabiogvdneto.warps.exception.WarpNotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandDelwarp extends CommandHandler<WarpsPlugin> {

    public CommandDelwarp(WarpsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));
            requireArguments(args, 1);

            plugin.getWarps().delete(args[0]);
            plugin.getMessages().warpDeleted(sender);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().commandUsage(sender, label);
        } catch (WarpNotFoundException e) {
            plugin.getMessages().warpNotFound(sender);
        }
    }
}
