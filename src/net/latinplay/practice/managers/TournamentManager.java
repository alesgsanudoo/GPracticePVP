package net.latinplay.practice.managers;

import net.latinplay.practice.match.MatchTeam;
import net.latinplay.practice.match.Match;
import net.latinplay.practice.party.Party;
import net.latinplay.practice.tournament.TournamentState;
import java.util.Iterator;
import org.bukkit.Bukkit;
import net.latinplay.practice.util.TeamUtil;
import net.latinplay.practice.tournament.TournamentTeam;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import net.latinplay.practice.runnable.TournamentRunnable;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import org.bukkit.scheduler.BukkitRunnable;
import net.latinplay.practice.tournament.Tournament;
import java.util.UUID;
import java.util.Map;
import net.latinplay.practice.Practice;

public class TournamentManager
{
    private final Practice plugin;
    private final Map<UUID, Integer> players;
    private final Map<UUID, Integer> matches;
    private final Map<Integer, Tournament> tournaments;
    private final Map<Tournament, BukkitRunnable> runnables;
    
    public TournamentManager() {
        this.plugin = Practice.getInstance();
        this.players = new HashMap<>();
        this.matches = new HashMap<>();
        this.tournaments = new HashMap<>();
        this.runnables = new HashMap<>();
    }
    
    public boolean isInTournament(UUID uuid) {
        return this.players.containsKey(uuid);
    }
    
    public Tournament getTournament(UUID uuid) {
        Integer id = this.players.get(uuid);
        if (id == null) {
            return null;
        }
        return this.tournaments.get(id);
    }
    
    public Tournament getTournamentFromMatch(UUID uuid) {
        Integer id = this.matches.get(uuid);
        if (id == null) {
            return null;
        }
        return this.tournaments.get(id);
    }
    
    public void createTournament(CommandSender commandSender, int id, int teamSize, int size, String kitName) {
        Tournament tournament = new Tournament(id, teamSize, size, kitName);
        this.tournaments.put(id, tournament);
        BukkitRunnable bukkitRunnable = new TournamentRunnable(tournament);
        bukkitRunnable.runTaskTimerAsynchronously(this.plugin, 20L, 20L);
        this.runnables.put(tournament, bukkitRunnable);
        commandSender.sendMessage(ChatColor.GREEN + "Torneo creado con éxito.");
        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            player.performCommand("tournament alert " + id);
        }
    }
    
    private void playerLeft(Tournament tournament, Player player) {
        TournamentTeam team = tournament.getPlayerTeam(player.getUniqueId());
        tournament.removePlayer(player.getUniqueId());
        player.sendMessage(ChatColor.GOLD.toString() + "(Evento) " + ChatColor.YELLOW + "Te fuiste del torneo.");
        this.players.remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        tournament.broadcast(ChatColor.GOLD.toString() + "(Evento) " + ChatColor.YELLOW + player.getName() + " dejó el torneo. (" + tournament.getPlayers().size() + "/" + tournament.getSize() + ")");
        if (team != null) {
            team.killPlayer(player.getUniqueId());
            if (team.getAlivePlayers().isEmpty()) {
                tournament.killTeam(team);
                if (tournament.getAliveTeams().size() == 1) {
                    TournamentTeam tournamentTeam = tournament.getAliveTeams().get(0);
                    String names = TeamUtil.getNames(tournamentTeam);
                    for (int i = 0; i <= 2; ++i) {
                        String announce = ChatColor.GOLD + "(Evento) " + ChatColor.GREEN.toString() + "Ganador: " + names + ".";
                        Bukkit.broadcastMessage(announce);
                    }
                    for (UUID playerUUID : tournamentTeam.getAlivePlayers()) {
                        this.players.remove(playerUUID);
                        Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
                        this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
                    }
                    this.plugin.getTournamentManager().removeTournament(tournament.getId(), false);
                }
            }
            else if (team.getLeader().equals(player.getUniqueId())) {
                team.setLeader(team.getAlivePlayers().get(0));
            }
        }
    }
    
    private void teamEliminated(Tournament tournament, TournamentTeam winnerTeam, TournamentTeam losingTeam) {
        for (UUID playerUUID : losingTeam.getAlivePlayers()) {
            Player player = this.plugin.getServer().getPlayer(playerUUID);
            tournament.removePlayer(player.getUniqueId());
            player.sendMessage(ChatColor.GOLD.toString() + "(Evento) " + ChatColor.YELLOW + "Has sido eliminado. " + ChatColor.GRAY);
            this.players.remove(player.getUniqueId());
        }
        String word = (losingTeam.getAlivePlayers().size() > 1) ? "tener" : "tiene";
        boolean isParty = tournament.getTeamSize() > 1;
        String announce = ChatColor.GOLD + "(Evento) " + ChatColor.RED + (isParty ? (losingTeam.getLeaderName() + " Party") : losingTeam.getLeaderName()) + ChatColor.GRAY + " " + word + " sido eliminado por " + ChatColor.GREEN + (isParty ? (winnerTeam.getLeaderName() + " Party") : winnerTeam.getLeaderName()) + ".";
        String alive = ChatColor.GOLD + "(Evento) " + ChatColor.GRAY + "Jugadores: (" + tournament.getPlayers().size() + "/" + tournament.getSize() + ")";
        tournament.broadcast(announce);
        tournament.broadcast(alive);
    }
    
    public void leaveTournament(Player player) {
        Tournament tournament = this.getTournament(player.getUniqueId());
        if (tournament == null) {
            return;
        }
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null && tournament.getTournamentState() != TournamentState.FIGHTING) {
            if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                for (UUID memberUUID : party.getMembers()) {
                    Player member = this.plugin.getServer().getPlayer(memberUUID);
                    this.playerLeft(tournament, member);
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "No eres el líder de la party.");
            }
        }
        else {
            this.playerLeft(tournament, player);
        }
    }
    
    private void playerJoined(Tournament tournament, Player player) {
        tournament.addPlayer(player.getUniqueId());
        this.players.put(player.getUniqueId(), tournament.getId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        tournament.broadcast(ChatColor.GOLD.toString() + "(Evento) " + ChatColor.YELLOW + player.getName() + " se unió al torneo. (" + tournament.getPlayers().size() + "/" + tournament.getSize() + ")");
    }
    
    public void joinTournament(Integer id, Player player) {
        Tournament tournament = this.tournaments.get(id);
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null) {
            if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                if (party.getMembers().size() + tournament.getPlayers().size() <= tournament.getSize()) {
                    if (party.getMembers().size() != tournament.getTeamSize() || party.getMembers().size() == 1) {
                        player.sendMessage(ChatColor.RED + "El tamaño de la fiesta debe ser de " + tournament.getTeamSize() + " jugadores.");
                    }
                    else {
                        for (UUID memberUUID : party.getMembers()) {
                            Player member = this.plugin.getServer().getPlayer(memberUUID);
                            this.playerJoined(tournament, member);
                        }
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "¡Lo siento! El torneo ya está lleno.");
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "YNo eres el líder de la party.");
            }
        }
        else {
            this.playerJoined(tournament, player);
        }
        if (tournament.getPlayers().size() == tournament.getSize()) {
            tournament.setTournamentState(TournamentState.STARTING);
        }
    }
    
    public Tournament getTournament(Integer id) {
        return this.tournaments.get(id);
    }
    
    public void removeTournament(Integer id, boolean force) {
        Tournament tournament = this.tournaments.get(id);
        if (tournament == null) {
            return;
        }
        if (force) {
            Iterator<UUID> pls = this.players.keySet().iterator();
            while (pls.hasNext()) {
                UUID uuid = pls.next();
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "El torneo terminado.");
                    Tournament tournament2 = tournament;
                    Player player2 = player;
                    this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                        if (tournament2.getTournamentState() == TournamentState.FIGHTING) {
                            this.plugin.getMatchManager().removeFighter(player2, this.plugin.getPlayerManager().getPlayerData(player2.getUniqueId()), false);
                        }
                        this.plugin.getPlayerManager().sendToSpawnAndReset(player2);
                        return;
                    }, 2L);
                }
                pls.remove();
            }
        }
        if (this.runnables.containsKey(tournament)) {
            this.runnables.get(tournament).cancel();
        }
        this.tournaments.remove(id);
    }
    
    public void addTournamentMatch(UUID matchId, Integer tournamentId) {
        this.matches.put(matchId, tournamentId);
    }
    
    public void removeTournamentMatch(Match match) {
        Tournament tournament = this.getTournamentFromMatch(match.getMatchId());
        if (tournament == null) {
            return;
        }
        tournament.removeMatch(match.getMatchId());
        this.matches.remove(match.getMatchId());
        MatchTeam losingTeam = (match.getWinningTeamId() == 0) ? match.getTeams().get(1) : match.getTeams().get(0);
        TournamentTeam losingTournamentTeam = tournament.getPlayerTeam(losingTeam.getPlayers().get(0));
        tournament.killTeam(losingTournamentTeam);
        MatchTeam winningTeam = match.getTeams().get(match.getWinningTeamId());
        TournamentTeam winningTournamentTeam = tournament.getPlayerTeam(winningTeam.getAlivePlayers().get(0));
        this.teamEliminated(tournament, winningTournamentTeam, losingTournamentTeam);
        if (tournament.getMatches().isEmpty()) {
            if (tournament.getAliveTeams().size() > 1) {
                tournament.setTournamentState(TournamentState.STARTING);
                tournament.setCurrentRound(tournament.getCurrentRound() + 1);
                tournament.setCountdown(16);
            }
            else {
                String names = TeamUtil.getNames(winningTournamentTeam);
                for (int i = 0; i <= 2; ++i) {
                    String announce = ChatColor.GOLD + "(Evento) " + ChatColor.GREEN.toString() + "Ganador: " + names + ".";
                    Bukkit.broadcastMessage(announce);
                }
                for (UUID playerUUID : winningTournamentTeam.getAlivePlayers()) {
                    this.players.remove(playerUUID);
                    Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
                    this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
                }
                this.plugin.getTournamentManager().removeTournament(tournament.getId(), false);
            }
        }
    }
    
    public Map<Integer, Tournament> getTournaments() {
        return this.tournaments;
    }
}
