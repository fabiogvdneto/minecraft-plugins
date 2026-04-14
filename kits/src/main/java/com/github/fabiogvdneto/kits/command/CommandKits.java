package com.github.fabiogvdneto.kits.command;

import com.github.fabiogvdneto.common.command.CommandHandler;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.exception.KitRecipientNotFoundException;
import com.github.fabiogvdneto.kits.kit.Kit;
import com.github.fabiogvdneto.kits.kit.KitRecipient;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CommandKits extends CommandHandler<KitPlugin> {

    public CommandKits(KitPlugin plugin) {
        super(plugin);
    }

    private String kitPermission() {
        return plugin.getSettings().getKitPermission();
    }

    private String kitPermission(String kitName) {
        return plugin.getSettings().getKitPermission(kitName);
    }

    private boolean hasKitPermission(Permissible permissible, String kitName) {
        return permissible.hasPermission(kitPermission(kitName)) || permissible.hasPermission(kitPermission());
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePermission(sender, plugin.getSettings().getCommandPermission(cmd));

            /*
            The goal here will be to insert the kits in cooldown first in the list,
            and insert the remaining kits last in the list. This way, we can visually
            inform the player which kits can be redeemed now, and which he can't.
             */

            Collection<Kit> kits = plugin.getKits().getAll();
            List<String> names;

            // Number of kits in cooldown.
            int cooldownCount = 0;

            if (sender instanceof Player player) {
                names = new LinkedList<>();

                for (Kit kit : kits) {
                    if (!hasKitPermission(sender, kit.getID())) continue;

                    try {
                        KitRecipient recipient = kit.getRecipient(player.getUniqueId());

                        if (recipient.getNextRedeemTime().isAfter(Instant.now())) {
                            cooldownCount++;
                            names.addFirst(kit.getID());
                            continue;
                        }
                    } catch (KitRecipientNotFoundException e) { /* ignore */ }

                    names.addLast(kit.getID());
                }
            } else {
                names = kits.stream().map(Kit::getID).toList();
            }

            plugin.getMessages().kitList(sender, names, cooldownCount);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        }
    }
}
