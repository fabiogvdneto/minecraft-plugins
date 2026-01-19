package com.github.fabiogvdneto.cursedwarps;

import com.github.fabiogvdneto.common.module.TranslationModuleBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public final class TranslationModule extends TranslationModuleBase {

    public TranslationModule(WarpPlugin plugin) {
        super(plugin, plugin.getSettings()::getLanguage);
    }

    /* ---- Misc ---- */

    public void playersOnly(Audience target) {
        message(target, "command.players-only");
    }

    public void playerNotFound(Audience target, String playerName) {
        message(target, "command.player-not-found", Placeholder.unparsed("player", playerName));
    }

    public void permissionRequired(Audience target) {
        message(target, "command.permission-required");
    }

    public void commandUsage(Audience target, String commandName) {
        message(target, "command.usage." + commandName);
    }

    /* ---- Teleporter ---- */

    public void movementNotAllowedDuringTeleportation(Audience target) {
        message(target, "teleporter.movement-not-allowed");
    }

    public void damageNotAllowedDuringTeleportation(Audience target) {
        message(target, "teleporter.damage-not-allowed");
    }

    public void teleportationCancelled(Audience target) {
        message(target, "teleporter.cancelled");
    }

    public void teleportedSuccessfully(Audience target) {
        message(target, "teleporter.success");
    }

    public void teleportationCountdown(Audience target, String remaining) {
        message(target, "teleporter.countdown", Placeholder.unparsed("seconds", remaining));
    }

    public void teleportationCountdown(Audience target, int remaining) {
        if (remaining == 0) {
            teleportedSuccessfully(target);
        } else {
            teleportationCountdown(target, Integer.toString(remaining));
        }
    }

    public void teleportationNotFound(Audience target) {
        message(target, "teleporter.player-not-found");
    }

    public void cannotTeleportBack(Audience target) {
        message(target, "teleporter.cannot-go-back");
    }

    /* ---- Teleportation Requests ---- */

    public void teleportationRequestSent(Audience target, String receiver) {
        message(target, "tpask.sent", Placeholder.unparsed("receiver", receiver));
    }

    public void teleportationRequestReceived(Audience target, String sender) {
        message(target, "tpask.received", Placeholder.parsed("sender", sender));
    }

    public void teleportationRequestExpired(Audience target, String sender) {
        message(target, "tpask.expired", Placeholder.unparsed("sender", sender));
    }

    public void teleportationRequestDenied(Audience target, String sender) {
        message(target, "tpask.denied", Placeholder.unparsed("sender", sender));
    }

    public void teleportationRequestAccepted(Audience target, String sender) {
        message(target, "tpask.accepted", Placeholder.unparsed("sender", sender));
    }

    public void teleportationRequestNotFound(Audience target) {
        message(target, "tpask.not-found");
    }

    public void teleportationRequestPending(Audience target) {
        message(target, "tpask.pending");
    }

    public void teleportationRequestCooldown(Audience target) {
        message(target, "tpask.cooldown");
    }

    public void teleportationRequestIgnored(Audience target) {
        message(target, "tpask.ignored");
    }

    public void teleportationRequestYourself(Audience target) {
        message(target, "tpask.yourself");
    }

    /* ---- Warps ---- */

    public void warpList(CommandSender target, Collection<String> warps) {
        if (warps.isEmpty()) {
            message(target, "warp.list.empty");
            return;
        }

        Component sep = component("warp.list.separator");
        Component list = warps.stream().map(Component::text).collect(Component.toComponent(sep));

        message(target, "warp.list.base", Placeholder.component("list", list));
    }

    public void warpSet(Audience target) {
        message(target, "warp.set");
    }

    public void warpDeleted(Audience target) {
        message(target, "warp.deleted");
    }

    public void warpNotFound(Audience target) {
        message(target, "warp.not-found");
    }

    public void warpAlreadyExists(Audience target) {
        message(target, "warp.already-exists");
    }

    /* ---- Homes ---- */

    public void homeListEmpty(Audience target) {
        message(target, "home.list.empty");
    }

    public void homeList(Audience target, Collection<String> homes) {
        if (homes.isEmpty()) {
            homeListEmpty(target);
            return;
        }

        Component sep = component("home.list.separator");
        Component list = homes.stream().map(Component::text).collect(Component.toComponent(sep));

        message(target, "home.list.base", Placeholder.component("list", list));
    }

    public void homeSet(Audience target) {
        message(target, "home.set");
    }

    public void homeDeleted(Audience target) {
        message(target, "home.deleted");
    }

    public void homeNotFound(Audience target) {
        message(target, "home.not-found");
    }

    public void homeAlreadyExists(Audience target) {
        message(target, "home.already-exists");
    }

    public void homeLimitReached(Audience target, int count, int limit) {
        message(target, "home.limit-reached",
                Placeholder.unparsed("count", Integer.toString(count)),
                Placeholder.unparsed("limit", Integer.toString(limit)));
    }

    /* ---- Spawn ---- */

    public void spawnNotFound(Audience target) {
        message(target, "spawn.undefined");
    }
}
