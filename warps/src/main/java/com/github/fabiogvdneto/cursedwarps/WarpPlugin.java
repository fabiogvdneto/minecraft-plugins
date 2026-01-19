package com.github.fabiogvdneto.cursedwarps;

import com.github.fabiogvdneto.cursedwarps.command.*;
import com.github.fabiogvdneto.cursedwarps.teleportation.TeleportationModule;
import com.github.fabiogvdneto.cursedwarps.teleportation.TeleportationService;
import com.github.fabiogvdneto.cursedwarps.user.UserModule;
import com.github.fabiogvdneto.cursedwarps.user.UserService;
import com.github.fabiogvdneto.cursedwarps.warp.WarpModule;
import com.github.fabiogvdneto.cursedwarps.warp.WarpService;
import org.bukkit.plugin.java.JavaPlugin;

public final class WarpPlugin extends JavaPlugin {

    public static WarpPlugin INSTANCE;

    private final ConfigurationModule settings = new ConfigurationModule(this);
    private final TranslationModule messages = new TranslationModule(this);
    private final TeleportationModule teleporter = new TeleportationModule(this);
    private final WarpModule warps = new WarpModule(this);
    private final UserModule users = new UserModule(this);

    /* ---- Bootstrap ---- */

    @Override
    public void onEnable() {
        WarpPlugin.INSTANCE = this;
        messages.load();
        settings.load();
        teleporter.load();
        warps.load();
        users.load();
        registerCommands();
    }

    @Override
    public void onDisable() {
        users.unload();
        warps.unload();
        teleporter.unload();
        messages.unload();
        settings.unload();
        WarpPlugin.INSTANCE = null;
    }

    private void registerCommands() {
        new CommandSpawn(this).register("spawn");
        new CommandWarp(this).register("warp");
        new CommandWarps(this).register("warps");
        new CommandDelwarp(this).register("delwarp");
        new CommandSetwarp(this).register("setwarp");
        new CommandHome(this).register("home");
        new CommandHomes(this).register("homes");
        new CommandSethome(this).register("sethome");
        new CommandDelhome(this).register("delhome");
        new CommandTpa(this).register("tpa");
        new CommandTphere(this).register("tphere");
        new CommandTpaccept(this).register("tpaccept");
        new CommandTpdeny(this).register("tpdeny");
        new CommandTpcancel(this).register("tpcancel");
    }

    /* ---- Modules ---- */

    public WarpService getWarps() {
        return warps;
    }

    public UserService getUsers() {
        return users;
    }

    public TeleportationService getTeleporter() {
        return teleporter;
    }

    public TranslationModule getMessages() {
        return messages;
    }

    public ConfigurationModule getSettings() {
        return settings;
    }
}
