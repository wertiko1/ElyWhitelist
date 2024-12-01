package me.wertiko.elyWhitelist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WhitelistCommands implements CommandExecutor, TabCompleter {
    private final ElyWhitelist plugin;
    private final Whitelist whitelist;
    private final PlainTextComponentSerializer plainTextSerializer = PlainTextComponentSerializer.plainText();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public WhitelistCommands(@NotNull ElyWhitelist plugin) {
        this.plugin = plugin;
        this.whitelist = plugin.whitelist;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(getConfigMessage("messages.notEnoughArguments"));
            return false;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "add":
                if (!sender.hasPermission("elywhitelist.add")) {
                    sender.sendMessage(getConfigMessage("noPermission"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(getConfigMessage("messages.notEnoughArguments"));
                    return false;
                }
                String playerToAdd = args[1];
                if (whitelist.addPlayer(playerToAdd)) {
                    sender.sendMessage(getConfigMessage("messages.playerAdded").replace("%player%", playerToAdd));
                } else {
                    sender.sendMessage(getConfigMessage("messages.playerAlreadyInWhitelist").replace("%player%", playerToAdd));
                }
                break;

            case "remove":
                if (!sender.hasPermission("elywhitelist.remove")) {
                    sender.sendMessage(getConfigMessage("noPermission"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(getConfigMessage("messages.notEnoughArguments"));
                    return false;
                }
                String playerToRemove = args[1];
                if (whitelist.removePlayer(playerToRemove)) {
                    sender.sendMessage(getConfigMessage("messages.playerRemoved").replace("%player%", playerToRemove));
                } else {
                    sender.sendMessage(getConfigMessage("messages.playerNotFound").replace("%player%", playerToRemove));
                }
                break;

            case "on":
                if (!sender.hasPermission("elywhitelist.on")) {
                    sender.sendMessage(getConfigMessage("noPermission"));
                    return false;
                }
                whitelist.setWhitelistEnabled(true);
                sender.sendMessage(getConfigMessage("messages.whitelist_enabled"));
                break;

            case "off":
                if (!sender.hasPermission("elywhitelist.off")) {
                    sender.sendMessage(getConfigMessage("noPermission"));
                    return false;
                }
                whitelist.setWhitelistEnabled(false);
                sender.sendMessage(getConfigMessage("messages.whitelistDisabled"));
                break;

            case "list":
                if (!sender.hasPermission("elywhitelist.list")) {
                    sender.sendMessage(getConfigMessage("noPermission"));
                    return false;
                }
                List<String> whitelistedPlayers = new ArrayList<>(whitelist.getAllWhitelistedPlayers());
                if (whitelistedPlayers.isEmpty()) {
                    sender.sendMessage(getConfigMessage("messages.whitelistEmpty"));
                } else {
                    String playerList = String.join(", ", whitelistedPlayers);
                    sender.sendMessage(getConfigMessage("messages.whitelistList").replace("%players%", playerList));
                }
                break;

            case "reload":
                if (!sender.hasPermission("elywhitelist.reload")) {
                    sender.sendMessage(getConfigMessage("noPermission"));
                    return false;
                }

                plugin.reloadConfig();
                plugin.config = plugin.getConfig();
                sender.sendMessage(getConfigMessage("messages.configReloaded"));
                break;

            default:
                sender.sendMessage(getConfigMessage("messages.unknownCommand"));
                return false;
        }

        return true;
    }

    private @NotNull String getConfigMessage(String path) {
        String message = plugin.getConfig().getString(path, "<red>Сообщение не найдено в конфигурации.");
        return plainTextSerializer.serialize(miniMessage.deserialize(message));
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions = Arrays.asList("add", "remove", "on", "off", "list", "reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            suggestions = new ArrayList<>(whitelist.getAllWhitelistedPlayers());
        }
        return suggestions;
    }
}
