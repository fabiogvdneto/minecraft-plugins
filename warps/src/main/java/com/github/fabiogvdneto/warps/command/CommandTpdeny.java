package com.github.fabiogvdneto.warps.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.CommandSenderException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.warps.WarpsPlugin;
import com.github.fabiogvdneto.warps.exception.TeleportationRequestClosedException;
import com.github.fabiogvdneto.warps.exception.TeleportationRequestNotFoundException;
import com.github.fabiogvdneto.warps.user.TeleportationRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandTpdeny extends CommandHandler<WarpsPlugin> {

    public CommandTpdeny(WarpsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));

            Player player = (Player) sender;
            Player target = (args.length == 0) ? player : parsePlayer(args, 0);

            plugin.getUsers().fetch(player.getUniqueId()).thenAccept(user -> {
                try {
                    user.getTeleportationRequest(target.getUniqueId()).deny();
                    plugin.getMessages().teleportationRequestDenied(player, target.getName());
                } catch (TeleportationRequestNotFoundException e) {
                    plugin.getMessages().teleportationRequestNotFound(player);
                } catch (TeleportationRequestClosedException e) {
                    plugin.getMessages().teleportationRequestExpired(player, target.getName());
                }
            });
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().playerNotFound(sender, args[0]);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return Collections.emptyList();

        Collection<TeleportationRequest> requests = plugin.getUsers().get(player.getUniqueId()).getTeleportationRequests();

        return requests.stream()
                .map(TeleportationRequest::getSender)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .map(Player::getName)
                .toList();
    }
}
