package com.github.fabiogvdneto.kits;

import com.github.fabiogvdneto.kits.command.CommandCreatekit;
import com.github.fabiogvdneto.kits.command.CommandDeletekit;
import com.github.fabiogvdneto.kits.command.CommandKit;
import com.github.fabiogvdneto.kits.command.CommandKits;
import com.github.fabiogvdneto.kits.kit.KitModule;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class KitPlugin extends JavaPlugin {

    private final ConfigurationModule settings = new ConfigurationModule(this);
    private final TranslationModule messages = new TranslationModule(this);
    private final KitModule kits = new KitModule(this);

    private Economy economy;

    @Override
    public void onEnable() {
        settings.load();
        messages.load();
        kits.load();
        registerCommands();
        setupEconomy();
    }

    @Override
    public void onDisable() {
        kits.unload();
        messages.unload();
        settings.unload();
    }

    private void registerCommands() {
        new CommandKit(this).register("kit");
        new CommandKits(this).register("kits");
        new CommandCreatekit(this).register("createkit");
        new CommandDeletekit(this).register("deletekit");
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            RegisteredServiceProvider<Economy> service = getServer().getServicesManager().getRegistration(Economy.class);

            if (service != null) {
                this.economy = service.getProvider();
            }
        }
    }

    public ConfigurationModule getSettings() {
        return settings;
    }

    public TranslationModule getMessages() {
        return messages;
    }

    public KitModule getKits() {
        return kits;
    }

    public Economy getEconomy() {
        return economy;
    }
}
