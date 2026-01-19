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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class TranslationModuleBase implements PluginModule {

    public static final String DEFAULT_LANGUAGE = "en";

    protected final Plugin plugin;
    protected final Map<String, String> translations;
    protected final Supplier<String> languageSupplier;

    private String language;

    public TranslationModuleBase(Plugin plugin, Supplier<String> languageSupplier) {
        this.plugin = Objects.requireNonNull(plugin);
        this.translations = new HashMap<>();
        this.languageSupplier = Objects.requireNonNull(languageSupplier);
    }

    public String getLanguage() {
        return language;
    }

    private void loadTranslations(Configuration config) {
        for (Map.Entry<String, Object> entry : config.getValues(true).entrySet()) {
            if (entry.getValue().getClass() == String.class) {
                translations.put(entry.getKey(), (String) entry.getValue());
            }
        }
    }

    private void loadTranslations(String language) {
        String path = "messages" + File.separatorChar + language + ".yml";

        this.loadTranslations(Plugins.loadConfigurationWithDefaults(plugin, path));
        this.plugin.getLogger().info("Loaded message translations at " + path + ".");
    }

    private String updateLanguage() {
        return this.language = languageSupplier.get().toLowerCase(Locale.ROOT);
    }

    @Override
    public void load() {
        translations.clear();
        loadTranslations(DEFAULT_LANGUAGE);

        if (!updateLanguage().equals(DEFAULT_LANGUAGE)) {
            loadTranslations(language);
        }
    }

    @Override
    public void unload() {
        translations.clear();
    }

    /* ---- Audience ---- */

    public Component component(String key) {
        return MiniMessage.miniMessage().deserialize(translations.get(key));
    }

    public Component component(String key, TagResolver resolver) {
        return MiniMessage.miniMessage().deserialize(translations.get(key), resolver);
    }

    public Component component(String key, TagResolver... resolvers) {
        return MiniMessage.miniMessage().deserialize(translations.get(key), resolvers);
    }

    public void message(Audience output, String key) {
        output.sendMessage(component(key));
    }

    public void message(Audience output, String key, TagResolver resolver) {
        output.sendMessage(component(key, resolver));
    }

    public void message(Audience output, String key, TagResolver... resolvers) {
        output.sendMessage(component(key, resolvers));
    }

    public void actionBar(Audience output, String key) {
        output.sendActionBar(component(key));
    }

    public void actionBar(Audience output, String key, TagResolver resolver) {
        output.sendActionBar(component(key, resolver));
    }

    public void actionBar(Audience output, String key, TagResolver... resolvers) {
        output.sendActionBar(component(key, resolvers));
    }
}
