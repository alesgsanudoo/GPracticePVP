package net.latinplay.practice.runnable;

import net.latinplay.practice.player.PlayerData;
import net.latinplay.practice.player.PlayerState;
import org.bukkit.entity.Player;
import net.latinplay.practice.kit.Kit;
import net.latinplay.practice.party.Party;
import java.util.Set;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import net.latinplay.practice.match.Match;
import net.latinplay.practice.queue.QueueType;
import net.latinplay.practice.match.MatchTeam;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import net.latinplay.practice.tournament.TournamentTeam;
import com.google.common.collect.Lists;
import java.util.UUID;
import com.google.common.collect.Sets;
import net.latinplay.practice.tournament.TournamentState;
import net.latinplay.practice.tournament.Tournament;
import net.latinplay.practice.Practice;
import org.bukkit.scheduler.BukkitRunnable;

public class TournamentRunnable extends BukkitRunnable
{
    private final Practice plugin;
    private final Tournament tournament;
    
    @Override
    public void run() {
        if (this.tournament.getTournamentState() == TournamentState.STARTING) {
            int countdown = this.tournament.decrementCountdown();
            if (countdown == 0) {
                if (this.tournament.getCurrentRound() == 1) {
                    Set<UUID> players = (Set<UUID>)Sets.newConcurrentHashSet((Iterable)this.tournament.getPlayers());
                    for (UUID player : players) {
                        Party party = this.plugin.getPartyManager().getParty(player);
                        if (party != null) {
                            TournamentTeam team = new TournamentTeam(party.getLeader(), Lists.newArrayList((Iterable)party.getMembers()));
                            this.tournament.addAliveTeam(team);
                            for (UUID member : party.getMembers()) {
                                players.remove(member);
                                this.tournament.setPlayerTeam(member, team);
                            }
                        }
                    }
                    List<UUID> currentTeam = null;
                    for (UUID player2 : players) {
                        if (currentTeam == null) {
                            currentTeam = new ArrayList<>();
                        }
                        currentTeam.add(player2);
                        if (currentTeam.size() == this.tournament.getTeamSize()) {
                            TournamentTeam team = new TournamentTeam(currentTeam.get(0), currentTeam);
                            this.tournament.addAliveTeam(team);
                            for (UUID teammate : team.getPlayers()) {
                                this.tournament.setPlayerTeam(teammate, team);
                            }
                            currentTeam = null;
                        }
                    }
                }
                List<TournamentTeam> teams = this.tournament.getAliveTeams();
                Collections.shuffle(teams);
                for (int i = 0; i < teams.size(); i += 2) {
                    TournamentTeam teamA = teams.get(i);
                    if (teams.size() > i + 1) {
                        TournamentTeam teamB = teams.get(i + 1);
                        for (UUID playerUUID : teamA.getAlivePlayers()) {
                            this.removeSpectator(playerUUID);
                        }
                        for (UUID playerUUID : teamB.getAlivePlayers()) {
                            this.removeSpectator(playerUUID);
                        }
                        MatchTeam matchTeamA = new MatchTeam(teamA.getLeader(), new ArrayList<>(teamA.getAlivePlayers()), 0);
                        MatchTeam matchTeamB = new MatchTeam(teamB.getLeader(), new ArrayList<>(teamB.getAlivePlayers()), 1);
                        Kit kit = this.plugin.getKitManager().getKit(this.tournament.getKitName());
                        Match match = new Match(this.plugin.getArenaManager().getRandomArena(kit), kit, QueueType.UNRANKED, matchTeamA, matchTeamB);
                        Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
                        Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());
                        match.broadcast(ChatColor.GOLD + "Starting tournament match. " + ChatColor.YELLOW + "(" + leaderA.getName() + " vs " + leaderB.getName() + ")");
                        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                            this.plugin.getMatchManager().createMatch(match);
                            this.tournament.addMatch(match.getMatchId());
                            this.plugin.getTournamentManager().addTournamentMatch(match.getMatchId(), this.tournament.getId());
                            return;
                        });
                    }
                    else {
                        for (UUID playerUUID2 : teamA.getAlivePlayers()) {
                            Player player3 = this.plugin.getServer().getPlayer(playerUUID2);
                            player3.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                            player3.sendMessage(ChatColor.GOLD + "You have been skipped to the next round.");
                            player3.sendMessage(ChatColor.GOLD + "There was no matching team for you.");
                            player3.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                        }
                    }
                }
                this.tournament.broadcast(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                this.tournament.broadcast(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "TOURNAMENT (" + this.tournament.getTeamSize() + "v" + this.tournament.getTeamSize() + ") " + this.tournament.getKitName());
                this.tournament.broadcast(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.YELLOW + "Starting Round #" + this.tournament.getCurrentRound());
                this.tournament.setTournamentState(TournamentState.FIGHTING);
            }
            else if (countdown <= 5) {
                String announce = ChatColor.GOLD + "(Tournament) " + ChatColor.GREEN + "Round #" + this.tournament.getCurrentRound() + " is starting in " + ChatColor.YELLOW + countdown + ChatColor.GREEN + ".";
                this.tournament.broadcast(announce);
            }
        }
    }
    
    private void removeSpectator(UUID playerUUID) {
        Player player = this.plugin.getServer().getPlayer(playerUUID);
        if (player != null) {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData.getPlayerState() == PlayerState.SPECTATING) {
                this.plugin.getMatchManager().removeSpectator(player);
            }
        }
    }
    
    public TournamentRunnable(Tournament tournament) {
        this.plugin = Practice.getInstance();
        this.tournament = tournament;
    }
}
