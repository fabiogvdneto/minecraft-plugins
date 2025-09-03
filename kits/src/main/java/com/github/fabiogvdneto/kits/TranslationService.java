package com.github.fabiogvdneto.kits;

import com.github.fabiogvdneto.common.PluginService;
import com.github.fabiogvdneto.common.lang.PluginTranslator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Collection;
import java.util.Objects;

public class TranslationService implements PluginService {

    private static final String DEFAULT_LANGUAGE = "en";

    private final KitsPlugin plugin;
    private final PluginTranslator translator;

    public TranslationService(KitsPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
        this.translator = new PluginTranslator();
    }

    @Override
    public void enable() {
        translator.clearTranslations();
        translator.loadTranslations(plugin, DEFAULT_LANGUAGE);
        translator.loadTranslations(plugin, plugin.getSettings().getLanguage());
    }

    @Override
    public void disable() {
        translator.clearTranslations();
    }

    private Component getMessage(String key) {
        return MiniMessage.miniMessage().deserialize(translator.get(key));
    }

    private void write(String key, Audience target) {
        target.sendMessage(getMessage(key));
    }

    private void write(String key, Audience target, TagResolver resolver) {
        target.sendMessage(
                MiniMessage.miniMessage().deserialize(translator.get(key), resolver)
        );
    }

    private void write(String key, Audience target, TagResolver... resolvers) {
        target.sendMessage(
                MiniMessage.miniMessage().deserialize(translator.get(key), resolvers)
        );
    }

    /* ---- Messages ---- */

    public void permissionRequired(Audience target) {
        write("permission-required", target);
    }

    public void commandPlayersOnly(Audience target) {
        write("command.players-only", target);
    }

    public void commandUsageKit(Audience target) {
        write("command.usage.kit", target);
    }

    public void commandUsageCreatekit(Audience target) {
        write("command.usage.createkit", target);
    }

    public void commandUsageDeletekit(Audience target) {
        write("command.usage.deletekit", target);
    }

    public void kitRedeemed(Audience target, String kitName) {
        write("kit.redeemed", target, Placeholder.unparsed("name", kitName));
    }

    public void kitInventoryFull(Audience target, String spaceRequired, String spaceAvailable) {
        write("kit.inventory-full", target,
                Placeholder.unparsed("required", spaceRequired),
                Placeholder.unparsed("available", spaceAvailable));
    }

    public void kitCreated(Audience target, String kitName) {
        write("kit.created", target, Placeholder.unparsed("name", kitName));
    }

    public void kitDeleted(Audience target, String kitName) {
        write("kit.deleted", target, Placeholder.unparsed("name", kitName));
    }

    public void kitNotFound(Audience target, String kitName) {
        write("kit.not-found", target, Placeholder.unparsed("name", kitName));
    }

    public void kitAlreadyExists(Audience target, String kitName) {
        write("kit.already-exists", target, Placeholder.unparsed("name", kitName));
    }

    public void kitCooldown(Audience target, String cooldownMinutes) {
        write("kit.cooldown", target, Placeholder.unparsed("cooldown", cooldownMinutes));
    }

    public void kitInsufficientMoney(Audience target, String cost) {
        write("kit.insufficient-money", target, Placeholder.unparsed("cost", cost));
    }

    public void kitListEmpty(Audience target) {
        write("kit.list.empty", target);
    }

    public void kitList(Audience target, Collection<String> kits) {
        Component separator = getMessage("kit.list.separator");
        Component list = kits.stream().map(Component::text).collect(Component.toComponent(separator));

        write("kit.list.base", target, Placeholder.component("list", list));
    }
}
