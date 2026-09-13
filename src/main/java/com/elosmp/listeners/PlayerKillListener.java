package com.elosmp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import com.elosmp.EloSMP;

public class PlayerKillListener implements Listener {

    private final EloSMP plugin;

    public PlayerKillListener(EloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (victim == null) return;

        // Handle victim losing Elo
        boolean shouldBan = plugin.getEloManager().removeElo(victim, 1);
        int victimNewElo = plugin.getEloManager().getElo(victim);
        plugin.getLeaderboardManager().updateLeaderboard(victim.getName(), victimNewElo);

        if (shouldBan) {
            plugin.getServer().broadcastMessage("§4§l[ZERO ELO BAN] §c" + victim.getName() + " reached 0 Elo and has been banned!");
            victim.kickPlayer("§cYou reached 0 Elo and have been banned from the server.");
            victim.setBanned(true);
        } else {
            victim.sendMessage("§c-1 Elo for dying. (Total: §6" + victimNewElo + "§c)");
        }

        // Handle killer gaining Elo
        if (killer != null && !killer.equals(victim)) {
            plugin.getEloManager().addElo(killer, 1);
            int killerNewElo = plugin.getEloManager().getElo(killer);
            plugin.getLeaderboardManager().updateLeaderboard(killer.getName(), killerNewElo);
            killer.sendMessage("§a+1 Elo for killing §f" + victim.getName() + "§a! (Total: §6" + killerNewElo + "§a)");
        }
    }
}
