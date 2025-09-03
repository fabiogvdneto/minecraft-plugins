package com.github.fabiogvdneto.warps.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandSenderException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.common.teleporter.Teleportation;
import com.github.fabiogvdneto.warps.WarpsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTpcancel extends CommandHandler<WarpsPlugin> {

    public CommandTpcancel(WarpsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));

            Player player = (Player) sender;
            Teleportation ongoing = plugin.getTeleporter().ongoing(player);

            if (ongoing == null) {
                plugin.getMessages().teleportationNotFound(player);
                return;
            }

            ongoing.cancel();
            plugin.getMessages().teleportationCancelled(player);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        }
    }
}
