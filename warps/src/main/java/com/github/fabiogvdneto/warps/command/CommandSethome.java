package com.github.fabiogvdneto.warps.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.CommandSenderException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.warps.WarpsPlugin;
import com.github.fabiogvdneto.warps.exception.HomeAlreadyExistsException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSethome extends CommandHandler<WarpsPlugin> {

    public CommandSethome(WarpsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));
            requireArguments(args, 1);

            Player player = (Player) sender;
            Player target = (args.length == 1 || !hasOthersPermission(player)) ? player : parsePlayer(args, 1);

            plugin.getUsers().fetch(target.getUniqueId(), user -> {
                if (player == target) {
                    int count = user.getHomes().size();
                    int limit = plugin.getSettings().getHomeLimit(player);

                    if (count >= limit) {
                        plugin.getMessages().homeLimitReached(player, count, limit);
                        return;
                    }
                }

                try {
                    user.createHome(args[0], player.getLocation());
                    plugin.getMessages().homeSet(sender);
                } catch (HomeAlreadyExistsException e) {
                    plugin.getMessages().homeAlreadyExists(sender);
                }
            });
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (CommandArgumentException e) {
            if (e.getIndex() == 1) {
                plugin.getMessages().playerNotFound(sender, args[1]);
            } else {
                plugin.getMessages().commandUsage(sender, label);
            }
        }
    }

    private boolean hasOthersPermission(Player player) {
        return player.hasPermission(plugin.getSettings().getAdminPermission());
    }
}
