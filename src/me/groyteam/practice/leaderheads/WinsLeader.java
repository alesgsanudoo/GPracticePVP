package me.groyteam.practice.leaderheads;

import java.util.Arrays;

import me.groyteam.practice.Practice;
import me.groyteam.practice.player.PlayerData;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

public class WinsLeader extends OnlineDataCollector{
    
    private Practice plugin;
    
    public WinsLeader(Practice plugin) {
	super("gpractice-wins", "GPractice", BoardType.DEFAULT, "ArenaPvP: Top Wins", "ArenaPvP Wins", Arrays.asList(null, null, "&3{amount} Ganadas.", null));
        this.plugin = plugin;
    }

    @Override
    public Double getScore(Player arg0) {
	PlayerData playerData = plugin.getPlayerManager().getPlayerData(arg0.getUniqueId());
	return (double) playerData.getRankedWins();
    }
}