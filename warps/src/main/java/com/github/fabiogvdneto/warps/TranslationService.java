package com.github.fabiogvdneto.warps;

import com.github.fabiogvdneto.common.PluginService;
import com.github.fabiogvdneto.common.lang.PluginTranslator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Objects;

public final class TranslationService implements PluginService {

    private static final String DEFAULT_LANGUAGE = "en";

    private final WarpsPlugin plugin;
    private final PluginTranslator translator;

    public TranslationService(WarpsPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
        this.translator = new PluginTranslator();
    }

    public void enable() {
        translator.clearTranslations();
        translator.loadTranslations(plugin, DEFAULT_LANGUAGE);
        translator.loadTranslations(plugin, plugin.getSettings().getLanguage());
    }

    public void disable() {
        translator.clearTranslations();
    }

    private Component getMessage(String key) {
        String translation = translator.get(key);
        return MiniMessage.miniMessage().deserialize(translation);
    }

    private void write(String key, Audience output) {
        String translation = translator.get(key);
        output.sendMessage(MiniMessage.miniMessage().deserialize(translation));
    }

    private void write(String key, Audience output, TagResolver resolver) {
        String translation = translator.get(key);
        output.sendMessage(MiniMessage.miniMessage().deserialize(translation, resolver));
    }

    private void write(String key, Audience output, TagResolver... resolvers) {
        String translation = translator.get(key);
        output.sendMessage(MiniMessage.miniMessage().deserialize(translation, resolvers));
    }

    /* ---- Misc ---- */

    public void playersOnly(Audience target) {
        write("command.players-only", target);
    }

    public void playerNotFound(Audience target, String playerName) {
        write("command.player-not-found", target, Placeholder.unparsed("player", playerName));
    }

    public void permissionRequired(Audience target) {
        write("command.permission-required", target);
    }

    public void commandUsage(Audience target, String commandName) {
        write("command.usage." + commandName, target);
    }

    /* ---- Teleporter ---- */

    public void movementNotAllowedWhileTeleporting(Audience target) {
        write("teleporter.movement-not-allowed", target);
    }

    public void damageNotAllowedWhileTeleporting(Audience target) {
        write("teleporter.damage-not-allowed", target);
    }

    public void teleportationCancelled(Audience target) {
        write("teleporter.cancelled", target);
    }

    public void teleportationCountdown(Audience target, String remaining) {
        write("teleporter.countdown", target, Placeholder.unparsed("seconds", remaining));
    }

    public void teleportedSuccessfully(Audience target) {
        write("teleporter.success", target);
    }

    public void teleportationNotFound(Audience target) {
        write("teleporter.player-not-found", target);
    }

    public void cannotTeleportBack(Audience target) {
        write("teleporter.cannot-go-back", target);
    }

    /* ---- Teleportation Requests ---- */

    public void teleportationRequestSent(Audience target, String receiver) {
        write("tpask.sent", target, Placeholder.unparsed("receiver", receiver));
    }

    public void teleportationRequestReceived(Audience target, String sender) {
        write("tpask.received", target, Placeholder.parsed("sender", sender));
    }

    public void teleportationRequestExpired(Audience target, String sender) {
        write("tpask.expired", target, Placeholder.unparsed("sender", sender));
    }

    public void teleportationRequestDenied(Audience target, String sender) {
        write("tpask.denied", target, Placeholder.unparsed("sender", sender));
    }

    public void teleportationRequestAccepted(Audience target, String sender) {
        write("tpask.accepted", target, Placeholder.unparsed("sender", sender));
    }

    public void teleportationRequestNotFound(Audience target) {
        write("tpask.not-found", target);
    }

    public void teleportationRequestPending(Audience target) {
        write("tpask.pending", target);
    }

    public void teleportationRequestCooldown(Audience target) {
        write("tpask.cooldown", target);
    }

    public void teleportationRequestIgnored(Audience target) {
        write("tpask.ignored", target);
    }

    public void teleportationRequestYourself(Audience target) {
        write("tpask.yourself", target);
    }

    /* ---- Warps ---- */

    public void warpList(CommandSender target, Collection<String> warps) {
        if (warps.isEmpty()) {
            write("warp.list.empty", target);
            return;
        }

        Component sep = getMessage("warp.list.separator");
        Component list = warps.stream().map(Component::text).collect(Component.toComponent(sep));

        write("warp.list.base", target, Placeholder.component("list", list));
    }

    public void warpSet(Audience target) {
        write("warp.set", target);
    }

    public void warpDeleted(Audience target) {
        write("warp.deleted", target);
    }

    public void warpNotFound(Audience target) {
        write("warp.not-found", target);
    }

    public void warpAlreadyExists(Audience target) {
        write("warp.already-exists", target);
    }

    /* ---- Homes ---- */

    public void homeListEmpty(Audience target) {
        write("home.list.empty", target);
    }

    public void homeList(Audience target, Collection<String> homes) {
        if (homes.isEmpty()) {
            homeListEmpty(target);
            return;
        }

        Component sep = getMessage("home.list.separator");
        Component list = homes.stream().map(Component::text).collect(Component.toComponent(sep));

        write("home.list.base", target, Placeholder.component("list", list));
    }

    public void homeSet(Audience target) {
        write("home.set", target);
    }

    public void homeDeleted(Audience target) {
        write("home.deleted", target);
    }

    public void homeNotFound(Audience target) {
        write("home.not-found", target);
    }

    public void homeAlreadyExists(Audience target) {
        write("home.already-exists", target);
    }

    public void homeLimitReached(Audience target, int count, int limit) {
        write("home.limit-reached", target,
                Placeholder.unparsed("count", Integer.toString(count)),
                Placeholder.unparsed("limit", Integer.toString(limit)));
    }

    /* ---- Spawn ---- */

    public void spawnNotFound(Audience target) {
        write("spawn.undefined", target);
    }
}
