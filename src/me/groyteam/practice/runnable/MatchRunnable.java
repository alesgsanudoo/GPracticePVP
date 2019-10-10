package me.groyteam.practice.runnable;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import me.groyteam.practice.match.MatchState;
import org.bukkit.ChatColor;
import me.groyteam.practice.match.Match;
import me.groyteam.practice.Practice;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchRunnable extends BukkitRunnable
{
    private final Practice plugin;
    private final Match match;
    
    @Override
    public void run() {
        switch (this.match.getMatchState()) {
            case STARTING: {
                if (this.match.decrementCountdown() != 0) {
                    this.match.broadcast(ChatColor.WHITE + "§fLa partida comienza en §3" + this.match.getCountdown() + " §fsegundo" + ((this.match.getCountdown() == 1) ? "" : "s") + "...");
                    break;
                }
                this.match.setMatchState(MatchState.FIGHTING);
                this.match.broadcast(ChatColor.GREEN + "§3§l¡A LUCHAR! §fLa partida ha comenzado.");
                if (this.match.isRedrover()) {
                    this.plugin.getMatchManager().pickPlayer(this.match);
                    break;
                }
                break;
            }
            case SWITCHING: {
                if (this.match.decrementCountdown() == 0) {
                    this.match.getEntitiesToRemove().forEach(Entity::remove);
                    this.match.clearEntitiesToRemove();
                    this.match.setMatchState(MatchState.FIGHTING);
                    this.plugin.getMatchManager().pickPlayer(this.match);
                    break;
                }
                break;
            }
            case ENDING: {
                if (this.match.decrementCountdown() == 0) {
                    this.plugin.getTournamentManager().removeTournamentMatch(this.match);
                    this.match.getRunnables().forEach(id -> this.plugin.getServer().getScheduler().cancelTask((int)id));
                    this.match.getEntitiesToRemove().forEach(Entity::remove);
                    this.match.getTeams().forEach(team -> team.alivePlayers().forEach(this.plugin.getPlayerManager()::sendToSpawnAndReset));
                    this.match.spectatorPlayers().forEach(this.plugin.getMatchManager()::removeSpectator);
                    this.match.getPlacedBlockLocations().forEach(location -> location.getBlock().setType(Material.AIR));
                    this.match.getOriginalBlockChanges().forEach(blockState -> blockState.getLocation().getBlock().setType(blockState.getType()));
                    this.plugin.getMatchManager().removeMatch(this.match);
                    new MatchResetRunnable(this.match).runTaskTimer(this.plugin, 20L, 20L);
                    this.cancel();
                    break;
                }
                break;
            }
        }
    }
    
    public MatchRunnable(final Match match) {
        this.plugin = Practice.getInstance();
        this.match = match;
    }
}
