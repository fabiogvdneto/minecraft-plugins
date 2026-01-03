package com.github.fabiogvdneto.warps.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.CommandSenderException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.warps.WarpsPlugin;
import com.github.fabiogvdneto.warps.exception.TeleportationRequestAlreadyExistsException;
import com.github.fabiogvdneto.warps.user.TeleportationRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class CommandTpa extends CommandHandler<WarpsPlugin> {

    public CommandTpa(WarpsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));
            requireArguments(args, 1);

            Player player = (Player) sender;
            Player target = parsePlayer(args, 0);

            if (player == target) {
                plugin.getMessages().teleportationRequestYourself(sender);
                return;
            }

            plugin.getUsers().fetch(target.getUniqueId()).thenAccept(user -> {
                try {
                    Duration duration = Duration.ofSeconds(plugin.getSettings().getTeleportationRequestDuration());

                    user.createTeleportationRequest(player.getUniqueId(), duration);
                    plugin.getMessages().teleportationRequestSent(player, target.getName());
                    plugin.getMessages().teleportationRequestReceived(target, player.getName());
                } catch (TeleportationRequestAlreadyExistsException e) {
                    TeleportationRequest request = e.getValue();

                    if (request.getState() == TeleportationRequest.State.OPEN) {
                        plugin.getMessages().teleportationRequestPending(player);
                    } else {
                        plugin.getMessages().teleportationRequestCooldown(player);
                    }
                }
            });
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (CommandArgumentException e) {
            if (e.getIndex() == 0) {
                plugin.getMessages().playerNotFound(sender, args[0]);
            } else {
                plugin.getMessages().commandUsage(sender, label);
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
        // Returning null will list all the online players.
        return (args.length == 1) ? null : Collections.emptyList();
    }
}
