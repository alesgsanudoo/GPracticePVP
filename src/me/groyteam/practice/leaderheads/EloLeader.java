package me.groyteam.practice.leaderheads;

import java.util.Arrays;

import me.groyteam.practice.Practice;
import me.groyteam.practice.player.PlayerData;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

public class EloLeader extends OnlineDataCollector{
    
    private Practice plugin;
    
    public EloLeader(Practice plugin) {
	super("gpractice-elo", "GPractice", BoardType.DEFAULT, "ArenaPvP: Top Elo", "ArenaPvP Elo", Arrays.asList(null, null, "&3{amount} Elo.", null));
        this.plugin = plugin;
    }

    @Override
    public Double getScore(Player arg0) {
	PlayerData playerData = plugin.getPlayerManager().getPlayerData(arg0.getUniqueId());
	return (double) playerData.getGlobalRankedElo();
    }
}
