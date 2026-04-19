package com.github.fabiogvdneto.kits.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.CommandSenderException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.exception.KitAlreadyExistsException;
import com.github.fabiogvdneto.kits.kit.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class CommandCreatekit extends CommandHandler<KitPlugin> {

    public CommandCreatekit(KitPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));
            requireArguments(args, 1);

            Kit kit = plugin.getKits().create(args[0]);
            Player player = (Player) sender;

            ItemStack[] contents = Arrays.stream(player.getInventory().getContents())
                    .filter(Objects::nonNull)
                    .toArray(ItemStack[]::new);

            kit.setContents(contents);
            plugin.getMessages().kitCreated(sender, kit.getName());
        } catch (CommandSenderException e) {
            plugin.getMessages().commandPlayersOnly(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().commandUsageCreatekit(sender);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (KitAlreadyExistsException e) {
            plugin.getMessages().kitAlreadyExists(sender, args[0]);
        }
    }
}
