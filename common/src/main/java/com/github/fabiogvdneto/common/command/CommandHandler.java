package com.github.fabiogvdneto.common.command;

import com.github.fabiogvdneto.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.common.exception.CommandSenderException;
import com.github.fabiogvdneto.common.exception.PermissionRequiredException;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class CommandHandler<P extends JavaPlugin> implements CommandExecutor, TabCompleter {

    protected final P plugin;

    public CommandHandler(P plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    /* ---- Registration ---- */

    /**
     * Register this command handler as an executor and tab completer of the given command.
     */
    public final void register(String label) {
        register(plugin.getCommand(label));
    }

    /**
     * Register this command handler as an executor and tab completer of the given command.
     */
    public final void register(PluginCommand command) {
        Objects.requireNonNull(command, "Can't register a null command.");
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    /* ---- Template Methods ---- */

    @Override
    public final boolean onCommand(@NotNull CommandSender sender,
                                   @NotNull Command command,
                                   @NotNull String label,
                                   @NotNull String[] args) {
        execute(sender, command, label, args);
        return true;
    }

    @Override @Nullable
    public final List<String> onTabComplete(@NotNull CommandSender sender,
                                            @NotNull Command command,
                                            @NotNull String label,
                                            @NotNull String[] args) {
        return complete(sender, command, label, args);
    }

    /* ---- Operations ---- */

    public abstract void execute(CommandSender sender, Command cmd, String label, String[] args);

    public List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
        // Default behaviour: completes with nothing.
        return Collections.emptyList();
    }

    /* ---- Validations ---- */

    /**
     * Throws CommandSenderException if the command sender is not a player.
     */
    public final void requirePlayer(CommandSender sender) throws CommandSenderException {
        if (!(sender instanceof Player))
            throw new CommandSenderException();
    }

    /**
     * Throws PermissionRequiredException if the sender does not have the given permission.
     */
    public final void requirePermission(CommandSender sender, String permission) throws PermissionRequiredException {
        if (!sender.hasPermission(permission))
            throw new PermissionRequiredException(permission);
    }

    /**
     * Throws CommandArgumentException if the arguments provided are not enough.
     * The index property is set to -1.
     */
    public final void requireArguments(String[] args, int minimumLength) throws CommandArgumentException {
        if (args.length < minimumLength)
            throw new CommandArgumentException(-1);
    }

    /**
     * Parses the argument at a given index to an integer.
     * Throws CommandArgumentException if not possible.
     * The index property is set to the given index.
     */
    public final int parseInt(String[] args, int index) throws CommandArgumentException {
        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {
            throw new CommandArgumentException(index);
        }
    }

    /**
     * Parses the argument at a given index to a long.
     * Throws CommandArgumentException if not possible.
     * The index property is set to the given index.
     */
    public final long parseLong(String[] args, int index) throws CommandArgumentException {
        try {
            return Long.parseLong(args[index]);
        } catch (NumberFormatException e) {
            throw new CommandArgumentException(index);
        }
    }

    /**
     * Parses the argument at a given index to a player.
     * Throws CommandArgumentException if not possible.
     * The index property is set to the given index.
     */
    public final Player parsePlayer(String[] args, int index) throws CommandArgumentException {
        Player player = Bukkit.getPlayer(args[index]);

        if (player == null)
            throw new CommandArgumentException(index);

        return player;
    }
}
