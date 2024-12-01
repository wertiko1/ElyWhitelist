package me.wertiko.elyWhitelist;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ElyWhitelist extends JavaPlugin implements Listener {
    public FileConfiguration config;
    public Whitelist whitelist;
    private WhitelistHttpServer httpServer;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        config = getConfig();
        whitelist = new Whitelist(this);
        int port = config.getInt("port", 8080);
        httpServer = new WhitelistHttpServer(port, whitelist, this);
        try {
            httpServer.start();
            getLogger().info("HTTP-сервер запущен на порту " + port);
        } catch (Exception e) {
            getLogger().severe("Не удалось запустить HTTP-сервер: " + e.getMessage());
        }

        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("elywl")).setExecutor(new WhitelistCommands(this));
        Objects.requireNonNull(getCommand("elywl")).setTabCompleter(new WhitelistCommands(this));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (!whitelist.isWhitelistEnabled()) return;
        if (!whitelist.isPlayerWhitelisted(event.getName())) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, MiniMessage.miniMessage().deserialize(config.getString("messages.notInWhitelist", "<red>Вас нет в вайтлисте!")));
        }
    }

    @Override
    public void onDisable() {
        httpServer.stop();
    }
}
