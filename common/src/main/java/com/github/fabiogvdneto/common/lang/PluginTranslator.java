package com.github.fabiogvdneto.common.lang;

import com.github.fabiogvdneto.common.Plugins;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PluginTranslator extends AbstractTranslator {

    private String code;

    public PluginTranslator() {
        super(new HashMap<>());
        clearTranslations();
    }

    @Override
    public String language() {
        return code;
    }

    private String buildPath() {
        return "messages" + File.separatorChar + code + ".yml";
    }

    public void loadTranslations(Plugin plugin, String code) {
        this.code = code;

        String path = buildPath();

        Configuration config = Plugins.createConfigurationFromResource(plugin, path);

        loadTranslations(config.getDefaults());
        loadTranslations(config);

        plugin.getLogger().info("Loaded message translations (" + path + ").");
    }

    private void loadTranslations(Configuration config) {
        for (Map.Entry<String, Object> entry : config.getValues(true).entrySet()) {
            if (entry.getValue().getClass() == String.class) {
                translations.put(entry.getKey(), (String) entry.getValue());
            }
        }
    }

    public void clearTranslations() {
        this.code = "empty";
        this.translations.clear();
    }
}