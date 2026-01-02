package com.github.fabiogvdneto.warps;

import com.github.fabiogvdneto.common.service.ConfigurationServiceBase;
import org.bukkit.command.Command;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ConfigurationService extends ConfigurationServiceBase {

    public ConfigurationService(Plugin plugin) {
        super(plugin);
    }

    /* ---- File Configuration ---- */

    public String getLanguage() {
        return config().getString("lang");
    }

    public int getTeleporterDelay(Permissible perm) {
        int max = config().getInt("teleporter.max-delay-seconds");

        return IntStream.range(0, max)
                .filter(i -> perm.hasPermission("warps.teleporter.delay." + i))
                .findFirst().orElse(max);
    }

    public int getTeleporterDelayForTpa(Permissible perm) {
        int delay = getTeleporterDelay(perm);
        int min = config().getInt("tpask.min-delay-seconds");

        return Math.max(min, delay);
    }

    public boolean isMovementAllowedWhileTeleporting() {
        return config().getBoolean("teleporter.movement-allowed");
    }

    public boolean isDamageAllowedWhileTeleporting() {
        return config().getBoolean("teleporter.damage-allowed");
    }

    public String getTeleportationCommandsAllowedMode() {
        return config().getString("teleporter.commands-allowed.mode");
    }

    public Set<String> getTeleportationCommandsAllowedList() {
        return config().getStringList("teleporter.commands-allowed.list").stream()
                .map(String::toLowerCase)
                .collect(Collectors.toUnmodifiableSet());
    }

    public int getTeleportationRequestDuration() {
        return config().getInt("tpask.duration-seconds");
    }

    public int getWarpAutosaveInterval() {
        return config().getInt("warps.autosave-minutes");
    }

    public int getUserAutosaveInterval() {
        return config().getInt("users.autosave-minutes");
    }

    public int getUserPurgeDays() {
        return config().getInt("users.purge-days");
    }

    public int getHomeLimit(Permissible perm) {
        int i = config().getInt("homes.max-limit");

        if (perm.hasPermission("warps.homes.limit.*")) return 99;
        if (perm.hasPermission("warps.homes.limit.max")) return i;

        while (i > 0 && !perm.hasPermission("warps.homes.limit." + i)) i--;

        return i;
    }

    /* ---- Permissions ---- */

    public String getAdminPermission() {
        return "warps.admin";
    }

    public String getCommandPermission(Command cmd) {
        return "warps.command." + cmd.getName();
    }

    public String getCommandPermission(String cmd) {
        return "warps.command." + cmd;
    }

    public String getWarpPermission(String warpName) {
        return "warps.warp." + warpName;
    }
}
