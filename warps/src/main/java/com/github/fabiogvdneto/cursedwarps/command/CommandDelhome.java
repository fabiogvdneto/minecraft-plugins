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

public class CommandDelhome extends CommandHandler<WarpPlugin> {

    public CommandDelhome(WarpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));
            requireArguments(args, 1);

            Player player = (Player) sender;

            plugin.getUsers().fetch(player.getUniqueId()).thenAccept(user -> {
                try {
                    user.deleteHome(args[0]);
                    plugin.getMessages().homeDeleted(player);
                } catch (HomeNotFoundException e) {
                    plugin.getMessages().homeNotFound(player);
                }
            });
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().commandUsage(sender, label);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player) || args.length > 1) return Collections.emptyList();

        Collection<Home> homes = plugin.getUsers().get(player.getUniqueId()).getHomes();
        return homes.stream().map(Home::getName).toList();
    }
}