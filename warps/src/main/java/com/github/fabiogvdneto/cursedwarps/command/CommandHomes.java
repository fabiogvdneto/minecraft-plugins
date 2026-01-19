package com.github.fabiogvdneto.cursedwarps.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.CommandSenderException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.cursedwarps.WarpPlugin;
import com.github.fabiogvdneto.cursedwarps.user.Home;
import com.github.fabiogvdneto.cursedwarps.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public class CommandHomes extends CommandHandler<WarpPlugin> {

    public CommandHomes(WarpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));

            Player player = (Player) sender;

            Consumer<User> callback = user -> {
                if (user == null) {
                    plugin.getMessages().homeListEmpty(sender);
                } else {
                    List<String> homes = user.getHomes().stream().map(Home::getName).toList();
                    plugin.getMessages().homeList(sender, homes);
                }
            };

            if (args.length == 0) {
                plugin.getUsers().fetch(player.getUniqueId()).thenAccept(callback);
            } else {
                Player target = parsePlayer(args, 0);
                plugin.getUsers().fetch(target.getUniqueId()).thenAccept(callback);
            }
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().playerNotFound(sender, args[0]);
        }
    }
}
