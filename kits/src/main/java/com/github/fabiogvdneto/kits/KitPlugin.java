package com.github.fabiogvdneto.kits;

import com.github.fabiogvdneto.kits.command.CommandCreatekit;
import com.github.fabiogvdneto.kits.command.CommandDeletekit;
import com.github.fabiogvdneto.kits.command.CommandKit;
import com.github.fabiogvdneto.kits.command.CommandKits;
import com.github.fabiogvdneto.kits.kit.KitManager;
import com.github.fabiogvdneto.kits.kit.KitService;
import org.bukkit.plugin.java.JavaPlugin;

public class KitPlugin extends JavaPlugin {

    private final ConfigurationService settings = new ConfigurationService(this);
    private final TranslationService messages = new TranslationService(this);
    private final KitService kits = new KitService(this);

    @Override
    public void onEnable() {
        settings.enable();
        messages.enable();
        kits.enable();
        registerCommands();
    }

    private void registerCommands() {
        new CommandCreatekit(this).registerAs("createkit");
        new CommandDeletekit(this).registerAs("deletekit");
        new CommandKits(this).registerAs("kits");
        new CommandKit(this).registerAs("kit");
    }

    @Override
    public void onDisable() {
        settings.disable();
        messages.disable();
        kits.disable();
    }

    public ConfigurationService getSettings() {
        return settings;
    }

    public TranslationService getMessages() {
        return messages;
    }

    public KitManager getKits() {
        return kits;
    }
}
