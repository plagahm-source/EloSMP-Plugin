package com.elosmp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import com.elosmp.EloSMP;

public class PlayerJoinListener implements Listener {

    private final EloSMP plugin;

    public PlayerJoinListener(EloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int elo = plugin.getEloManager().getElo(player);

        // Update leaderboard with current Elo
        plugin.getLeaderboardManager().updateLeaderboard(player.getName(), elo);

        player.sendMessage("§6§l========== Welcome to EloSMP! ==========");
        player.sendMessage("§aYour current Elo: §6" + elo);
        player.sendMessage("§bCommands:");
        player.sendMessage("  §f/elotop [page] §7- View top players");
        player.sendMessage("  §f/elorank [player] §7- Check your rank");
        player.sendMessage("§6§l==========================================");
    }
}
