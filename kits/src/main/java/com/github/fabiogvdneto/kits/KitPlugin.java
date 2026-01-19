package com.github.fabiogvdneto.kits;

import com.github.fabiogvdneto.kits.command.CommandCreatekit;
import com.github.fabiogvdneto.kits.command.CommandDeletekit;
import com.github.fabiogvdneto.kits.command.CommandKit;
import com.github.fabiogvdneto.kits.command.CommandKits;
import com.github.fabiogvdneto.kits.kit.KitModule;
import com.github.fabiogvdneto.kits.kit.KitService;
import org.bukkit.plugin.java.JavaPlugin;

public class KitPlugin extends JavaPlugin {

    private final ConfigurationModule settings = new ConfigurationModule(this);
    private final TranslationModule messages = new TranslationModule(this);
    private final KitModule kits = new KitModule(this);

    @Override
    public void onEnable() {
        settings.load();
        messages.load();
        kits.load();
        registerCommands();
    }

    @Override
    public void onDisable() {
        settings.unload();
        messages.unload();
        kits.unload();
    }

    private void registerCommands() {
        new CommandCreatekit(this).register("createkit");
        new CommandDeletekit(this).register("deletekit");
        new CommandKits(this).register("kits");
        new CommandKit(this).register("kit");
    }

    public ConfigurationModule getSettings() {
        return settings;
    }

    public TranslationModule getMessages() {
        return messages;
    }

    public KitService getKits() {
        return kits;
    }
}
