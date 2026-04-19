package com.github.fabiogvdneto.kits.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.exception.KitNotFoundException;
import com.github.fabiogvdneto.kits.kit.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandDeletekit extends CommandHandler<KitPlugin> {

    public CommandDeletekit(KitPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));
            requireArguments(args, 1);

            Kit kit = plugin.getKits().delete(args[0]);
            plugin.getMessages().kitDeleted(sender, kit.getName());
        } catch (CommandArgumentException e) {
            plugin.getMessages().commandUsageDeletekit(sender);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (KitNotFoundException e) {
            plugin.getMessages().kitNotFound(sender, args[0]);
        }
    }
}
