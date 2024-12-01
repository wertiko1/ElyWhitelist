package me.wertiko.elyWhitelist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class Whitelist {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Set<String> whitelistedPlayers = new HashSet<>();
    private final ElyWhitelist plugin;
    private final File whitelistFile;
    private boolean whitelistEnabled;

    public Whitelist(@NotNull ElyWhitelist plugin) {
        this.plugin = plugin;
        File pluginFolder = plugin.getDataFolder();
        this.whitelistFile = new File(pluginFolder, "whitelist.json");

        loadWhitelist();
        whitelistEnabled = plugin.getConfig().getBoolean("whitelistEnabled", false);
    }

    public boolean addPlayer(String playerName) {
        boolean added = whitelistedPlayers.add(playerName.toLowerCase());
        if (added) {
            saveWhitelist();
        }
        return added;
    }

    public boolean removePlayer(String playerName) {
        boolean removed = whitelistedPlayers.remove(playerName.toLowerCase());
        if (removed) {
            saveWhitelist();
        }
        return removed;
    }

    public boolean isPlayerWhitelisted(String playerName) {
        return whitelistedPlayers.contains(playerName.toLowerCase());
    }

    public Set<String> getAllWhitelistedPlayers() {
        return new HashSet<>(whitelistedPlayers);
    }

    public boolean isWhitelistEnabled() {
        return whitelistEnabled;
    }

    public void setWhitelistEnabled(boolean is_enabled) {
        whitelistEnabled = is_enabled;
        plugin.getConfig().set("whitelistEnabled", is_enabled);
        plugin.saveConfig();
    }

    private void loadWhitelist() {
        if (!whitelistFile.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(whitelistFile)) {
            Type setType = new TypeToken<Set<String>>() {
            }.getType();
            Set<String> loadedPlayers = GSON.fromJson(reader, setType);
            if (loadedPlayers != null) {
                whitelistedPlayers.addAll(loadedPlayers);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке вайтлиста: " + e.getMessage());
        }
    }

    private void saveWhitelist() {
        try (FileWriter writer = new FileWriter(whitelistFile)) {
            GSON.toJson(whitelistedPlayers, writer);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении вайтлиста: " + e.getMessage());
        }
    }
}
