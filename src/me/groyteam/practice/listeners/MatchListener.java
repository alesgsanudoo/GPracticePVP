package me.groyteam.practice.listeners;

import java.util.Map;

import io.github.beelzebu.coins.api.CoinsAPI;
import me.groyteam.practice.CustomLocation;
import me.groyteam.practice.Practice;
import me.groyteam.practice.arena.Arena;
import me.groyteam.practice.event.match.MatchEndEvent;
import me.groyteam.practice.event.match.MatchStartEvent;
import me.groyteam.practice.file.Config;
import me.groyteam.practice.kit.Kit;
import me.groyteam.practice.match.Match;
import me.groyteam.practice.match.MatchState;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.player.PlayerState;
import me.groyteam.practice.runnable.MatchRunnable;
import me.groyteam.practice.util.Clickable;
import me.groyteam.practice.util.EloUtil;
import me.groyteam.practice.util.PlayerUtil;

import java.util.LinkedHashMap;
import me.groyteam.practice.inventory.InventorySnapshot;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import spg.lgdev.iSpigot;
import spg.lgdev.knockback.Knockback;

public class MatchListener implements Listener
{
    private final Practice plugin;

    public MatchListener() {
        this.plugin = Practice.getInstance();
    }

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        Match match = event.getMatch();
        Kit kit = match.getKit();
        if (!kit.isEnabled()) {
            match.broadcast(ChatColor.RED + "Este kit está actualmente deshabilitado.");
            this.plugin.getMatchManager().removeMatch(match);
            return;
        }
        if (kit.isBuild() || kit.isSpleef()) {
            Arena arena = event.getMatch().getArena();
            if (arena.getAvailableArenas().isEmpty()) {
                match.broadcast(ChatColor.RED + "No hay arenas disponibles.");
                this.plugin.getMatchManager().removeMatch(match);
                return;
            }
            match.setStandaloneArena(arena.getAvailableArena());
            arena.removeAvailableArena(arena.getAvailableArena());
            this.plugin.getArenaManager().setArenaMatchUUID(match.getStandaloneArena(), match.getMatchId());
        }
        Set<Player> matchPlayers = new HashSet<>();
        Set<Player> set = matchPlayers;
        Match match2 = match;
        Kit kit2 = kit;
        match.getTeams().forEach(team -> team.alivePlayers().forEach(player -> {
            set.add(player);
            this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            player.setAllowFlight(false);
            player.setFlying(false);
            if(match.getType().isRanked()) {
                int newRanked = playerData.getRankeds() - 1;
                playerData.setRankeds(newRanked);
            }
            playerData.setCurrentMatchID(match2.getMatchId());
            playerData.setTeamID(team.getTeamID());
            playerData.setMissedPots(0);
            playerData.setLongestCombo(0);
            playerData.setCombo(0);
            playerData.setHits(0);
            PlayerUtil.clearPlayer(player);
            CustomLocation locationA = ((match2.getStandaloneArena() != null) ? match2.getStandaloneArena().getA() : match2.getArena().getA());
            CustomLocation locationB = ((match2.getStandaloneArena() != null) ? match2.getStandaloneArena().getB() : match2.getArena().getB());
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport((team.getTeamID() == 1) ? locationA.toBukkitLocation() : locationB.toBukkitLocation());
                }
            });
            player.sendMessage(ChatColor.DARK_RED.toString() + "§3§lArenaPvP §8» §c" + ChatColor.RED + "El uso de Butterfly puede ser considerado AutoClick y puede pasar a una sanción.");
            if (kit2.isBuild()) {
                player.sendMessage(ChatColor.DARK_RED.toString() + "§3§lArenaPvP §8» §c" + ChatColor.RED + "Se prohibe totalmente campear o de lo contrario seras sancionado.");
            }
            Knockback combo = iSpigot.INSTANCE.getKnockbackHandler().getKnockbackProfile("Combo");
            Knockback sumo = iSpigot.INSTANCE.getKnockbackHandler().getKnockbackProfile("Sumo");
            Knockback pots = iSpigot.INSTANCE.getKnockbackHandler().getKnockbackProfile("Pots");
            Knockback normal = iSpigot.INSTANCE.getKnockbackHandler().getDefaultKnockback();
            Knockback sumo2 = iSpigot.INSTANCE.getKnockbackHandler().getKnockbackProfile("Sumo");
            if(kit2.isCombo()) {
                player.setKnockback(combo);
            } else if(kit2.isSumo()) {
                player.setKnockback(sumo2);
            } else if(player.getLocation().getWorld().getName().equals("event")) {
                player.setKnockback(sumo);
            } else if(player.getLocation().getWorld().getName().equals("pots")) {
                player.setKnockback(pots);
            } else {
                player.setKnockback(normal);
            }
            if (!match2.isRedrover()) {
                this.plugin.getMatchManager().giveKits(player, kit2);
                playerData.setPlayerState(PlayerState.FIGHTING);
            }
            else {
                this.plugin.getMatchManager().addRedroverSpectator(player, match2);
            }
        }));
        for (Player player : matchPlayers) {
            for (Player online : this.plugin.getServer().getOnlinePlayers()) {
                online.hidePlayer(player);
                player.hidePlayer(online);
            }
        }

        for (Player player : matchPlayers) {
            for (Player other : matchPlayers) {
                player.showPlayer(other);
            }
        }
        new MatchRunnable(match).runTaskTimerAsynchronously(this.plugin, 20L, 20L);
    }


    private double getChance() {
        return Math.random() * 100.0;
    }


    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        match.broadcast("");
        match.broadcast("§3§lEstadisticas de la partida:");
        match.broadcast("");
        Clickable winnerClickable = new Clickable(ChatColor.GREEN + "§2● §aGanador(es): ");
        Clickable loserClickable = new Clickable(ChatColor.RED + "§2● §cPerdedor(es): ");
        match.setMatchState(MatchState.ENDING);
        match.setWinningTeamId(event.getWinningTeam().getTeamID());
        match.setCountdown(4);
        if (match.isFFA()) {
            Player winner = this.plugin.getServer().getPlayer(event.getWinningTeam().getAlivePlayers().get(0));
            Match match2 = match;
            Player player2 = winner;
            Clickable clickable = winnerClickable;
            Clickable clickable2 = loserClickable;
            event.getWinningTeam().players().forEach(player -> {
                if (!match2.hasSnapshot(player.getUniqueId())) {
                    match2.addSnapshot(player);
                }
                if (player.getUniqueId() == player2.getUniqueId()) {
                    clickable.add(ChatColor.GRAY + player.getName() + " ", ChatColor.WHITE + "Click para ver el inventario.", "/inventory " + match2.getSnapshot(player.getUniqueId()).getSnapshotId());
                }
                else {
                    clickable2.add(ChatColor.GRAY + player.getName() + " ", ChatColor.WHITE + "Click para ver el inventario.", "/inventory " + match2.getSnapshot(player.getUniqueId()).getSnapshotId());
                }
            });
            for (InventorySnapshot snapshot : match.getSnapshots().values()) {
                this.plugin.getInventoryManager().addSnapshot(snapshot);
            }
            match.broadcast(winnerClickable);
            match.broadcast(loserClickable);
        }
        else if (match.isRedrover()) {
            match.broadcast(ChatColor.GREEN + "§fEl jugador §3"+ event.getWinningTeam().getLeaderName() + ChatColor.GRAY + " §f ha ganado el redrover.");
        }
        else {
            Map<UUID, InventorySnapshot> inventorySnapshotMap = new LinkedHashMap<>();
            Match match3 = match;
            Map<UUID, InventorySnapshot> map = inventorySnapshotMap;
            Clickable clickable3 = winnerClickable;
            Clickable clickable4 = loserClickable;
            match.getTeams().forEach(team -> team.players().forEach(player -> {
                if (!match3.hasSnapshot(player.getUniqueId())) {
                    match3.addSnapshot(player);
                }
                map.put(player.getUniqueId(), match3.getSnapshot(player.getUniqueId()));
                boolean onWinningTeam = (this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getTeamID() == event.getWinningTeam().getTeamID());
                if (onWinningTeam) {
                    clickable3.add(ChatColor.WHITE + player.getName() + " ", ChatColor.WHITE + "Click para ver el inventario.", "/inventory " + match3.getSnapshot(player.getUniqueId()).getSnapshotId());
                }
                else {
                    clickable4.add(ChatColor.WHITE + player.getName() + " ", ChatColor.WHITE + "Click para ver el inventario.", "/inventory " + match3.getSnapshot(player.getUniqueId()).getSnapshotId());
                }
                player.setMaximumNoDamageTicks(20);
            }));
            for (InventorySnapshot snapshot : match.getSnapshots().values()) {
                this.plugin.getInventoryManager().addSnapshot(snapshot);
            }
            match.broadcast(winnerClickable);
            match.broadcast(loserClickable);
            if(match.getType().isUnranked()) {
                Player winnerLeader = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(0));
                PlayerData winnerLeaderData = this.plugin.getPlayerManager().getPlayerData(winnerLeader.getUniqueId());
                Config config = new Config("/players/" + winnerLeaderData.getUniqueId().toString(), this.plugin);
                if(config.getConfig().isSet("winunrankeds")) {
                    if(config.getConfig().getInt("winunrankeds") == 10) {
                        int rankeds = winnerLeaderData.getRankeds() + 5;
                        winnerLeaderData.setRankeds(rankeds);
                    } else {
                        int wins = config.getConfig().getInt("winunrankeds") + 1;
                        config.getConfig().set("winunrankeds", wins);
                    }
                } else {
                    config.getConfig().set("winunrankeds", 1);
                }
            }
            if (match.getType().isRanked()) {
                String kitName = match.getKit().getName();
                Player winnerLeader = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(0));
                PlayerData winnerLeaderData = this.plugin.getPlayerManager().getPlayerData(winnerLeader.getUniqueId());
                Player loserLeader = this.plugin.getServer().getPlayer(event.getLosingTeam().getPlayers().get(0));
                PlayerData loserLeaderData = this.plugin.getPlayerManager().getPlayerData(loserLeader.getUniqueId());
                int[] preElo = new int[2];
                int[] newElo = new int[2];
                CoinsAPI.addCoins(winnerLeader.getName(), 120);
                int winnerElo = 0;
                int loserElo = 0;
                int newWinnerElo = 0;
                int newLoserElo = 0;
                String eloMessage;
                winnerElo = winnerLeaderData.getElo(kitName);
                loserElo = loserLeaderData.getElo(kitName);
                preElo[0] = winnerElo;
                preElo[1] = loserElo;
                newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
                newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);
                newElo[0] = newWinnerElo;
                newElo[1] = newLoserElo;
                eloMessage = ChatColor.WHITE + "§2● §fCambios de Elo: " + ChatColor.GREEN + winnerLeader.getName() + " +" + (newWinnerElo - winnerElo) + " (" + newWinnerElo + ") " + ChatColor.WHITE + "vs "+ ChatColor.RED + loserLeader.getName() + " " + (newLoserElo - loserElo) + " (" + newLoserElo + ")";
                winnerLeaderData.setElo(kitName, newWinnerElo);
                loserLeaderData.setElo(kitName, newLoserElo);
                winnerLeaderData.setWins(kitName, winnerLeaderData.getWins(kitName) + 1);
                loserLeaderData.setLosses(kitName, loserLeaderData.getLosses(kitName) + 1);
                match.broadcast(eloMessage);
                match.broadcast("");
                winnerLeader.sendMessage("§fHas ganado §a120 §fpuntos por ganar la partida.");
                loserLeader.sendMessage("§fNo has ganado ningun punto.");
                if (this.getChance() < 30) {
                    Practice.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "mysteryvault add " + winnerLeader.getName() + " 1");
                }
            }
            //this.plugin.getMatchManager().saveRematches(match);
        }
        match.broadcast("");
        match.broadcast("§f¡Gracias por jugar a §3§lGPractice v1.2§f!");
        match.broadcast("");
    }
}
