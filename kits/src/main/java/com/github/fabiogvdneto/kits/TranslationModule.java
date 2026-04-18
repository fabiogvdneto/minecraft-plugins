package com.github.fabiogvdneto.kits;

import com.github.fabiogvdneto.common.module.TranslationModuleBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.Arrays;
import java.util.Collection;

public final class TranslationModule extends TranslationModuleBase {

    public TranslationModule(KitPlugin plugin) {
        super(plugin);
    }

    /* ---- Messages ---- */

    public void permissionRequired(Audience target) {
        message(target, "permission-required");
    }

    public void commandPlayersOnly(Audience target) {
        message(target, "command.players-only");
    }

    public void commandUsageKit(Audience target) {
        message(target, "command.usage.kit");
    }

    public void commandUsageCreatekit(Audience target) {
        message(target, "command.usage.createkit");
    }

    public void commandUsageDeletekit(Audience target) {
        message(target, "command.usage.deletekit");
    }

    public void kitRedeemed(Audience target, String kitName) {
        message(target, "kit.redeemed", Placeholder.unparsed("name", kitName));
    }

    public void kitInventoryFull(Audience target, String spaceRequired, String spaceAvailable) {
        message(target, "kit.inventory-full",
                Placeholder.unparsed("required", spaceRequired),
                Placeholder.unparsed("available", spaceAvailable));
    }

    public void kitCreated(Audience target, String kitName) {
        message(target, "kit.created", Placeholder.unparsed("name", kitName));
    }

    public void kitDeleted(Audience target, String kitName) {
        message(target, "kit.deleted", Placeholder.unparsed("name", kitName));
    }

    public void kitNotFound(Audience target, String kitName) {
        message(target, "kit.not-found", Placeholder.unparsed("name", kitName));
    }

    public void kitAlreadyExists(Audience target, String kitName) {
        message(target, "kit.already-exists", Placeholder.unparsed("name", kitName));
    }

    public void kitCooldown(Audience target, String cooldownMinutes) {
        message(target, "kit.error-cooldown", Placeholder.unparsed("cooldown", cooldownMinutes));
    }

    public void kitLimit(Audience target, String redeemLimit) {
        message(target, "kit.error-limit", Placeholder.unparsed("limit", redeemLimit));
    }

    public void kitInsufficientFunds(Audience target, String price) {
        message(target, "kit.error-insufficient-funds", Placeholder.unparsed("price", price));
    }

    public void kitListEmpty(Audience target) {
        message(target, "kit.list.empty");
    }

    public void kitList(Audience target, Collection<String> kits, int cooldownCount) {
        if (kits.isEmpty()) {
            kitListEmpty(target);
            return;
        }

        // Add strikethrough to kits in cooldown.
        TextComponent[] kitsComp = kits.stream().map(Component::text).toArray(TextComponent[]::new);
        for (int i = 0; i < cooldownCount; i++) {
            kitsComp[i] = kitsComp[i].decorate(TextDecoration.STRIKETHROUGH);
        }

        Component separator = component("kit.list.separator").orElse(Component.empty());
        Component list = Arrays.stream(kitsComp).collect(Component.toComponent(separator));

        message(target, "kit.list.base", Placeholder.component("list", list));
    }
}
