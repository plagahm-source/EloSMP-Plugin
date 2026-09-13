package com.elosmp.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardManager {

    private final JavaPlugin plugin;
    private final File leaderboardFile;
    private FileConfiguration leaderboard;

    public LeaderboardManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.leaderboardFile = new File(plugin.getDataFolder(), "leaderboard.yml");
        loadLeaderboard();
    }

    /**
     * Loads the leaderboard from disk.
     */
    private void loadLeaderboard() {
        if (!leaderboardFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                leaderboardFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create leaderboard file: " + e.getMessage());
            }
        }
        leaderboard = YamlConfiguration.loadConfiguration(leaderboardFile);
    }

    /**
     * Updates a player's entry in the leaderboard.
     */
    public void updateLeaderboard(String playerName, int elo) {
        leaderboard.set(playerName, elo);
        saveLeaderboard();
    }

    /**
     * Saves the leaderboard to disk.
     */
    private void saveLeaderboard() {
        try {
            leaderboard.save(leaderboardFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save leaderboard: " + e.getMessage());
        }
    }

    /**
     * Gets the top N players by Elo.
     */
    public LinkedList<Map.Entry<String, Integer>> getTopPlayers(int count) {
        return leaderboard.getKeys(false).stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> leaderboard.getInt(key, 0)
                ))
                .entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(count)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Gets a player's rank (1-indexed) and their Elo.
     */
    public Map<String, Object> getPlayerRank(String playerName) {
        List<Map.Entry<String, Integer>> sorted = leaderboard.getKeys(false).stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> leaderboard.getInt(key, 0)
                ))
                .entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getKey().equalsIgnoreCase(playerName)) {
                result.put("rank", i + 1);
                result.put("elo", sorted.get(i).getValue());
                return result;
            }
        }

        result.put("rank", -1);
        result.put("elo", 0);
        return result;
    }
}
