package com.github.fabiogvdneto.common.module;

import com.github.fabiogvdneto.common.Plugins;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class TranslationModuleBase implements PluginModule {

    public static final String DEFAULT_LANGUAGE = "en";

    protected final Plugin plugin;
    protected final Map<String, String> translations;

    protected String language = DEFAULT_LANGUAGE;

    public TranslationModuleBase(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
        this.translations = new HashMap<>();
    }

    public String getLanguage() {
        return language;
    }

    private void loadTranslations(Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (entry.getValue().getClass() == String.class) {
                String message = (String) entry.getValue();

                if (!message.isBlank()) {
                    translations.put(entry.getKey(), message);
                }
            }
        }
    }

    private void loadTranslations(Configuration config) {
        Configuration defaults = config.getDefaults();

        if (defaults != null) {
            loadTranslations(defaults.getValues(true));
        }

        loadTranslations(config.getValues(true));
    }

    private void loadTranslations(String languageCode) {
        String path = "messages" + File.separatorChar + languageCode + ".yml";
        loadTranslations(Plugins.loadConfigurationWithDefaults(plugin, path));

        plugin.getLogger().info("Loaded message translations at " + path + ".");
    }

    @Override
    public void load() {
        unload();
        loadTranslations(DEFAULT_LANGUAGE);

        String language = plugin.getConfig().getString("language");

        if (language != null && !language.equals(DEFAULT_LANGUAGE)) {
            this.loadTranslations(language);
            this.language = language;
        }
    }

    @Override
    public void unload() {
        translations.clear();
    }

    /* ---- Audience ---- */

    public Optional<Component> component(String key) {
        return Optional
                .ofNullable(translations.get(key))
                .map(MiniMessage.miniMessage()::deserialize);
    }

    public Optional<Component> component(String key, TagResolver resolver) {
        return Optional
                .ofNullable(translations.get(key))
                .map(value -> MiniMessage.miniMessage().deserialize(value, resolver));
    }

    public Optional<Component> component(String key, TagResolver... resolvers) {
        return Optional
                .ofNullable(translations.get(key))
                .map(value -> MiniMessage.miniMessage().deserialize(value, resolvers));
    }

    public void message(Audience output, String key) {
        component(key).ifPresent(output::sendMessage);
    }

    public void message(Audience output, String key, TagResolver resolver) {
        component(key, resolver).ifPresent(output::sendMessage);
    }

    public void message(Audience output, String key, TagResolver... resolvers) {
        component(key, resolvers).ifPresent(output::sendMessage);
    }

    public void actionBar(Audience output, String key) {
        component(key).ifPresent(output::sendActionBar);
    }

    public void actionBar(Audience output, String key, TagResolver resolver) {
        component(key, resolver).ifPresent(output::sendActionBar);
    }

    public void actionBar(Audience output, String key, TagResolver... resolvers) {
        component(key, resolvers).ifPresent(output::sendActionBar);
    }
}
