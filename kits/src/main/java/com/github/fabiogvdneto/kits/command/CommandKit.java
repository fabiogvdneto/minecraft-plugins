package com.github.fabiogvdneto.kits.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.CommandSenderException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.exception.InventoryFullException;
import com.github.fabiogvdneto.kits.exception.KitCooldownException;
import com.github.fabiogvdneto.kits.exception.KitNotFoundException;
import com.github.fabiogvdneto.kits.kit.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class CommandKit extends CommandHandler<KitPlugin> {

    public CommandKit(KitPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));
            requireArguments(args, 1);

            Kit kit = plugin.getKits().get(args[0]);

            requirePermission(sender, plugin.getSettings().getKitPermission(kit.getName()));

            boolean checkCooldown = sender.hasPermission(plugin.getSettings().getCooldownBypassPermission());
            boolean checkPrice = sender.hasPermission(plugin.getSettings().getPriceBypassPermission());

            // TODO: check price

            Player player = (Player) sender;
            UUID playerID = player.getUniqueId();

            if (checkCooldown) {
                kit.checkCooldown(playerID);
            }

            kit.collect(player.getInventory());

            if (checkCooldown) {
                kit.applyCooldown(playerID);
            }

            plugin.getMessages().kitRedeemed(sender, kit.getName());
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().commandPlayersOnly(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().commandUsageKit(sender);

            // TODO: list all kits available.
        } catch (KitNotFoundException e) {
            plugin.getMessages().kitNotFound(sender, args[0]);
        } catch (KitCooldownException e) {
            long cooldown = Instant.now().until(e.whenAvailable(), ChronoUnit.MINUTES);
            plugin.getMessages().kitCooldown(sender, Long.toString(cooldown));
        } catch (InventoryFullException e) {
            String required = Integer.toString(e.getSpaceRequired());
            String available = Integer.toString(e.getSpaceAvailable());
            plugin.getMessages().kitInventoryFull(sender, required, available);
        }
    }
}
