package com.github.fabiogvdneto.cursedwarps.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.CommandSenderException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.cursedwarps.WarpPlugin;
import com.github.fabiogvdneto.cursedwarps.exception.HomeNotFoundException;
import com.github.fabiogvdneto.cursedwarps.user.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CommandHome extends CommandHandler<WarpPlugin> {

    public CommandHome(WarpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));
            requireArguments(args, 1);

            Player player = (Player) sender;
            Player target = (args.length == 1) ? player : parsePlayer(args, 1);

            plugin.getUsers().fetch(target.getUniqueId()).thenAccept(user -> {
                try {
                    Home home = user.getHome(args[0]);

                    plugin.getTeleporter().teleport(player, home.getLocation());
                } catch (HomeNotFoundException e) {
                    plugin.getMessages().homeNotFound(sender);
                }
            });
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (CommandArgumentException e) {
            if (e.getIndex() == 1) {
                // The requested player is not online.
                plugin.getMessages().playerNotFound(sender, args[1]);
            } else {
                // Command was executed without arguments.
                plugin.getServer().dispatchCommand(sender, "homes");
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player) || args.length > 2) return Collections.emptyList();

        // Returning null will list all the online players.
        if (args.length == 2) return null;

        Collection<Home> homes = plugin.getUsers().get(player.getUniqueId()).getHomes();
        return homes.stream().map(Home::getName).toList();
    }
}
