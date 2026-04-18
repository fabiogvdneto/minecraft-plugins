package com.github.fabiogvdneto.kits.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.CommandSenderException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.exception.InventoryFullException;
import com.github.fabiogvdneto.kits.exception.KitCooldownException;
import com.github.fabiogvdneto.kits.exception.KitLimitException;
import com.github.fabiogvdneto.kits.exception.KitNotFoundException;
import com.github.fabiogvdneto.kits.kit.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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

            if (!sender.hasPermission(plugin.getSettings().getKitPermission())) {
                requirePermission(sender, plugin.getSettings().getKitPermission(kit.getID()));
            }

            boolean admin = sender.hasPermission(plugin.getSettings().getAdminPermission());

            // TODO: check price

            Player player = (Player) sender;

            if (admin) {
                kit.collect(player.getInventory());
            } else {
                kit.redeem(player);
            }

            plugin.getMessages().kitRedeemed(sender, kit.getID());
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().commandPlayersOnly(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().commandUsageKit(sender);
            plugin.getServer().dispatchCommand(sender, "kits");
        } catch (KitNotFoundException e) {
            plugin.getMessages().kitNotFound(sender, args[0]);
        } catch (KitCooldownException e) {
            long cooldown = Instant.now().until(e.whenAvailable(), ChronoUnit.MINUTES);
            plugin.getMessages().kitCooldown(sender, String.valueOf(cooldown));
        } catch (KitLimitException e) {
            plugin.getMessages().kitCooldown(sender, String.valueOf(e.getLimit()));
        } catch (InventoryFullException e) {
            String required = String.valueOf(e.getSpaceRequired());
            String available = String.valueOf(e.getSpaceAvailable());
            plugin.getMessages().kitInventoryFull(sender, required, available);
        }
    }
}
