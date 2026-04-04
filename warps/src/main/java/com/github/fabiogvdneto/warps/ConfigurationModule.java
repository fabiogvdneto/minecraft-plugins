package com.github.fabiogvdneto.warps;

import com.github.fabiogvdneto.common.module.ConfigurationModuleBase;
import org.bukkit.command.Command;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ConfigurationModule extends ConfigurationModuleBase {

    private static final String PERMISSION_PREFIX = "warps.";

    public ConfigurationModule(Plugin plugin) {
        super(plugin);
    }

    /* ---- Configuration ---- */

    public String getLanguage() {
        return config().getString("lang");
    }

    public int getTeleportationDelay(Permissible perm) {
        int max = config().getInt("teleporter.max-delay-seconds");

        return IntStream.range(0, max)
                .filter(i -> perm.hasPermission(getTeleporterDelayPermission(i)))
                .findFirst().orElse(max);
    }

    public int getTeleportationDelayWithMinimum(Permissible perm) {
        int delay = getTeleportationDelay(perm);
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
        return config().getString("teleporter.commands-allowed.mode").toLowerCase(Locale.ROOT);
    }

    public Set<String> getTeleportationCommandsAllowedList() {
        return config().getStringList("teleporter.commands-allowed.list").stream()
                .map(String::toLowerCase)
                .collect(Collectors.toUnmodifiableSet());
    }

    public int getTeleportationRequestDuration() {
        return config().getInt("tpask.duration-seconds");
    }

    public int getUserAutosaveInterval() {
        return config().getInt("users.autosave-minutes");
    }

    public int getUserPurgeDays() {
        return config().getInt("users.purge-days");
    }

    public int getHomeLimit(Permissible perm) {
        int i = config().getInt("homes.max-limit");

        if (perm.hasPermission(getHomesLimitPermission())) return 99;

        while (i > 0 && !perm.hasPermission(getHomesLimitPermission(i))) i--;

        return i;
    }

    /* ---- Permissions ---- */

    public String getAdminPermission() {
        return PERMISSION_PREFIX + "*";
    }

    public String getCommandPermission(Command cmd) {
        return PERMISSION_PREFIX + "command." + cmd.getName();
    }

    public String getCommandPermission(String cmd) {
        return PERMISSION_PREFIX + "command." + cmd;
    }

    public String getWarpPermission(String warpName) {
        return PERMISSION_PREFIX + "warp." + warpName;
    }

    public String getHomesLimitPermission() {
        return PERMISSION_PREFIX + "homes.limit.*";
    }

    public String getHomesLimitPermission(int limit) {
        return PERMISSION_PREFIX + "homes.limit." + limit;
    }

    public String getTeleporterDelayPermission(int delay) {
        return PERMISSION_PREFIX + "teleporter.delay." + delay;
    }
}
