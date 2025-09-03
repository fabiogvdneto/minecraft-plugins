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
        this.code = "null";
    }

    @Override
    public String language() {
        return code;
    }

    public void clearTranslations() {
        translations.clear();
    }

    public void loadTranslations(Plugin plugin, String code) {
        if (this.code.equals(code)) return;

        String path = path(code);

        Configuration config = Plugins.createConfigurationFromResource(plugin, path);

        for (Map.Entry<String, Object> entry : config.getDefaults().getValues(true).entrySet()) {
            if (entry.getValue().getClass() == String.class) {
                translations.put(entry.getKey(), (String) entry.getValue());
            }
        }

        for (Map.Entry<String, Object> entry : config.getValues(true).entrySet()) {
            if (entry.getValue().getClass() == String.class) {
                translations.put(entry.getKey(), (String) entry.getValue());
            }
        }

        this.code = code;
        plugin.getLogger().info("Loaded message translations (" + path + ").");
    }

    private String path(String code) {
        return "messages" + File.separatorChar + code + ".yml";
    }
}