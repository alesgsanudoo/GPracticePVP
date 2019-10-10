package me.groyteam.practice.board;

import io.github.beelzebu.coins.api.CoinsAPI;
import me.groyteam.practice.cache.StatusCache;
import me.groyteam.practice.events.EventState;
import me.groyteam.practice.events.PracticeEvent;
import me.groyteam.practice.events.oitc.OITCEvent;
import me.groyteam.practice.events.oitc.OITCPlayer;
import me.groyteam.practice.events.parkour.ParkourEvent;
import me.groyteam.practice.events.parkour.ParkourPlayer;
import me.groyteam.practice.events.redrover.RedroverPlayer;
import me.groyteam.practice.events.sumo.SumoEvent;
import me.groyteam.practice.events.sumo.SumoPlayer;
import me.groyteam.practice.match.MatchTeam;
import me.groyteam.practice.party.Party;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.player.PlayerState;
import me.groyteam.practice.queue.QueueEntry;
import me.groyteam.practice.queue.QueueType;
import me.groyteam.practice.match.Match;
import me.groyteam.practice.tournament.Tournament;

import java.util.UUID;

import me.groyteam.practice.events.redrover.RedroverEvent;
import me.groyteam.practice.util.PlayerUtil;
import java.util.StringJoiner;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.LinkedList;

import me.groyteam.practice.settings.item.ProfileOptionsItemState;
import java.util.List;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import java.util.Set;
import com.bizarrealex.aether.scoreboard.Board;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.groyteam.practice.Practice;
import com.bizarrealex.aether.scoreboard.BoardAdapter;

import java.util.logging.Level;

import static io.github.beelzebu.coins.api.CoinsAPI.getCoins;

public class PracticeBoard implements BoardAdapter {
    private final Practice plugin;


    public PracticeBoard() {
        this.plugin = Practice.getInstance();
    }

    @Override
    public String getTitle(final Player player) {
        return String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "§3§lArenaPvP ";
    }

    @Override
    public List<String> getScoreboard(final Player player, final Board board, final Set<BoardCooldown> cooldowns) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().log(Level.WARNING, "{0}''s player data is null", String.valueOf(player.getName()));
            return null;
        }
        if (playerData.getOptions().getScoreboard() == ProfileOptionsItemState.DISABLED) {
            return null;
        }
        switch (playerData.getPlayerState()) {
            case LOADING:
            case SPAWN:
            case EDITING:
            case SPECTATING:
            case FFA:
            case EVENT: {
                return this.getLobbyBoard(player, false);
            }
            case QUEUE: {
                return this.getLobbyBoard(player, true);
            }
            case FIGHTING: {
                return this.getGameBoard(player);
            }
            default: {
                return null;
            }
        }
    }

    private List<String> getLobbyBoard(final Player player, final boolean queuing) {
        final List<String> strings = new LinkedList<>();
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);
        if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
            event = this.plugin.getEventManager().getSpectators().get(player.getUniqueId());
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (event == null) {
            strings.add("");
            strings.add("§fNombre: §b" + player.getName());
            strings.add("§fPuntos: §b" + CoinsAPI.getCoins(player.getUniqueId()));
            strings.add("");
        }
        if (queuing) {
            QueueEntry queueEntry = this.plugin.getQueueManager().getQueueEntry(player.getUniqueId());
            if (queueEntry != null) {
                strings.add("§fEsperando: " + ChatColor.AQUA + "(" + queueEntry.getQueueType().getName() + ")");
                strings.add(ChatColor.WHITE + "Kit: §a§l" + queueEntry.getKitName());
                if (queueEntry.getQueueType() != QueueType.UNRANKED) {
                    final long queueTime = System.currentTimeMillis() - ((party == null) ? this.plugin.getQueueManager().getPlayerQueueTime(player.getUniqueId()) : this.plugin.getQueueManager().getPlayerQueueTime(party.getLeader()));
                    int eloRange = playerData.getEloRange();
                    final int seconds = Math.round(queueTime / 1000L);
                    if (seconds > 5 && eloRange != -1) {
                        eloRange += seconds * 50;
                        if (eloRange >= 4000) {
                            eloRange = 4000;
                        }
                    }
                    int elo = 800;
                    if (queueEntry.getQueueType() == QueueType.RANKED) {
                        elo = playerData.getElo(queueEntry.getKitName());
                    }
                    final String eloRangeString = "[" + Math.max(elo - eloRange / 2, 0) + " -> " + Math.max(elo + eloRange / 2, 0) + "]";
                    strings.add(ChatColor.WHITE + "Nivel: §b" + eloRangeString);
                    strings.add(ChatColor.WHITE + "División: " + playerData.getRankFromElo());
                    strings.add("");
                }
            }
        }
        if (party != null) {
            strings.add(ChatColor.YELLOW + "§fParty : " + ChatColor.AQUA + "(" + party.getMembers().size() + " jugador" + ((party.getMembers().size() == 1) ? "" : "es") + ")");
            strings.add(ChatColor.WHITE + "Lider: " + ChatColor.DARK_AQUA + Bukkit.getPlayer(party.getLeader()).getName());
            strings.add("");
        }
        if (event == null) {
            strings.add("§fEn Juego§f: §b" + StatusCache.getInstance().getFighting());
            strings.add("§fEn Espera§f: §b" + StatusCache.getInstance().getQueueing());
            strings.add("");
            strings.add("§fConectados: §b" + this.plugin.getServer().getOnlinePlayers().size());
            strings.add("");
            strings.add("§3mc.groyland.net");

        }
        if (event != null) {
            strings.add("");
            strings.add("§fNombre: §b" + player.getName());
            strings.add("§fPing: §b" + PlayerUtil.getPing(player));
            strings.add("§fEvento: §b" + event.getName());
            strings.add("");
            if (event instanceof SumoEvent) {
                final SumoEvent sumoEvent = (SumoEvent) event;
                final int playingSumo = sumoEvent.getByState(SumoPlayer.SumoState.WAITING).size() + sumoEvent.getByState(SumoPlayer.SumoState.FIGHTING).size() + sumoEvent.getByState(SumoPlayer.SumoState.PREPARING).size();
                strings.add("Jugadores: §3" + playingSumo + "/" + event.getLimit());
                final int countdown = sumoEvent.getCountdownTask().getTimeUntilStart();
                if (countdown > 0 && countdown <= 60) {
                    strings.add(ChatColor.WHITE + "Comienza en: §b" + countdown + "s");
                    strings.add("");
                }
                if (sumoEvent.getPlayer(player) != null) {
                    final SumoPlayer sumoPlayer = sumoEvent.getPlayer(player);
                    strings.add("§fEstado: §3" + StringUtils.capitalize(sumoPlayer.getState().name().toLowerCase()));
                    strings.add("");
                    strings.add("§3mc.groyland.net");
                }
            } else if (event instanceof ParkourEvent) {
                final ParkourEvent parkourEvent = (ParkourEvent) event;
                final int playingParkour = parkourEvent.getByState(ParkourPlayer.ParkourState.WAITING).size() + parkourEvent.getByState(ParkourPlayer.ParkourState.INGAME).size();
                strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Players§7: " + playingParkour + "/" + event.getLimit());
                final int countdown = parkourEvent.getCountdownTask().getTimeUntilStart();
                if (countdown > 0 && countdown <= 60) {
                    strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Starting§7: " + countdown + "s");
                }
                if (parkourEvent.getPlayer(player) != null) {
                    final ParkourPlayer parkourPlayer = parkourEvent.getPlayer(player);
                    if (parkourPlayer.getLastCheckpoint() != null && parkourPlayer.getCheckpointId() > 0) {
                        strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Checkpoint§7: #" + parkourPlayer.getCheckpointId());
                    }
                }
            } else if (event instanceof RedroverEvent) {
                final RedroverEvent redroverEvent = (RedroverEvent) event;
                final int playingParkour = redroverEvent.getByState(RedroverPlayer.RedroverState.WAITING).size() + redroverEvent.getByState(RedroverPlayer.RedroverState.FIGHTING).size();
                strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Players§7: " + playingParkour + "/" + event.getLimit());
                final int countdown = redroverEvent.getCountdownTask().getTimeUntilStart();
                if (countdown > 0 && countdown <= 60) {
                    strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Starting§7: " + countdown + "s");
                }
                if (redroverEvent.getPlayer(player) != null) {
                    final RedroverPlayer redroverPlayer = redroverEvent.getPlayer(player);
                    strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "State§7: " + StringUtils.capitalize(redroverPlayer.getState().name().toLowerCase()));
                }
                if (redroverEvent.getFighting().size() > 0) {
                    final StringJoiner joiner = new StringJoiner(ChatColor.YELLOW + " vs " + ChatColor.GOLD);
                    for (final UUID fighterUUID : redroverEvent.getFighting()) {
                        final Player fighter2 = Bukkit.getPlayer(fighterUUID);
                        if (fighter2 != null) {
                            joiner.add(fighter2.getName());
                        }
                    }
                    strings.add(ChatColor.GOLD + joiner.toString());
                }
            } else if (event instanceof OITCEvent) {
                final OITCEvent oitcEvent = (OITCEvent) event;
                final int playingOITC = oitcEvent.getPlayers().size();
                strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Players§7: " + playingOITC + "/" + event.getLimit());
                final int countdown = oitcEvent.getCountdownTask().getTimeUntilStart();
                if (countdown > 0 && countdown <= 60) {
                    strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Starting§7: " + countdown + "s");
                }
                if (oitcEvent.getPlayer(player) != null) {
                    final OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
                    if (oitcPlayer.getState() == OITCPlayer.OITCState.FIGHTING || oitcPlayer.getState() == OITCPlayer.OITCState.RESPAWNING) {
                        strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Kills§7: " + oitcPlayer.getScore());
                        strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Lives§7: " + oitcPlayer.getLives());
                    }
                }
                final List<OITCPlayer> sortedList = oitcEvent.sortedScores();
                if (sortedList.size() >= 2 && event.getState() == EventState.STARTED) {
                    strings.add(String.valueOf(ChatColor.YELLOW.toString()) + ChatColor.BOLD + "TOP KILLS");
                    final Player first = Bukkit.getPlayer(sortedList.get(0).getUuid());
                    final Player second = Bukkit.getPlayer(sortedList.get(1).getUuid());
                    if (first != null) {
                        strings.add(ChatColor.WHITE + "[1] " + first.getName() + "§7: §a" + sortedList.get(0).getScore());
                    }
                    if (second != null) {
                        strings.add(ChatColor.WHITE + "[2] " + second.getName() + "§7: §a" + sortedList.get(1).getScore());
                    }
                    if (sortedList.size() >= 3) {
                        final Player third = Bukkit.getPlayer(sortedList.get(2).getUuid());
                        if (third != null) {
                            strings.add(ChatColor.WHITE + "[3] " + ChatColor.WHITE + third.getName() + "§7: §a" + sortedList.get(2).getScore());
                        }
                    }
                }
            }
        }
        if (playerData.getPlayerState() != PlayerState.EVENT && this.plugin.getTournamentManager().getTournaments().size() >= 1) {
            for (final Tournament tournament : this.plugin.getTournamentManager().getTournaments().values()) {
                strings.add(ChatColor.YELLOW + "Tournament " + ChatColor.GRAY + "(" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + ")");
                strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Ladder§7: " + tournament.getKitName());
                strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Stage§7: " + "Round #" + tournament.getCurrentRound());
                strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Players§7: " + tournament.getPlayers().size() + "/" + tournament.getSize());
                final int countdown = tournament.getCountdown();
                if (countdown > 0 && countdown <= 30) {
                    strings.add(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "* " + ChatColor.WHITE + "Starting§7: " + countdown + "s");
                }
            }
        }
        return strings;
    }

    private List<String> getGameBoard(final Player player) {
        final List<String> strings = new LinkedList<>();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        strings.add("");
        strings.add("§fNombre: §b" + player.getName());
        strings.add("§fPing: §3" + PlayerUtil.getPing(player));
        strings.add("§fDivisión: " + playerData.getRankFromElo());
            strings.add("");
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            strings.add("§fKit: §a§l" + ((match.getKit() == null) ? "§cN/A" : match.getKit().getName()));
            strings.add("");
            Player opponentPlayer = null;
            if (!match.isPartyMatch() && !match.isFFA()) {
                opponentPlayer = ((match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId()) ? this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0)) : this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0)));
                if (opponentPlayer == null) {
                    return this.getLobbyBoard(player, false);
                }
                if (!match.isPartyMatch() && !match.isFFA()) {
                    final PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponentPlayer.getUniqueId());
                    if (opponentData != null) {
                        strings.add("§fRival: §3" + opponentPlayer.getName());
                        strings.add("§fPing Rival: §3" + PlayerUtil.getPing(opponentPlayer));
                        strings.add("§fDivisión: " + opponentData.getRankFromElo());
                        strings.add("");
                        strings.add("§3mc.groyland.net");

                    }
                }

            } else if (match.isPartyMatch() && !match.isFFA() && opponentPlayer != null) {
                final MatchTeam opposingTeam = match.isFFA() ? match.getTeams().get(0) : ((playerData.getTeamID() == 0) ? match.getTeams().get(1) : match.getTeams().get(0));
                final MatchTeam playerTeam = match.getTeams().get(playerData.getTeamID());
                strings.add("§fTu Equipo: §3" + playerTeam.getAlivePlayers().size() + " Vivos");
                final PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponentPlayer.getUniqueId());
                if (opponentData != null) {
                    strings.add("§fEquipo Rival: §3" + opposingTeam.getAlivePlayers().size() + " Vivos");
                }
            } else if (match.isFFA()) {
                final int alive = match.getTeams().get(0).getAlivePlayers().size() - 1;
                strings.add("§fRestante: §3" + match.getTeams().get(0).getAlivePlayers().size() + " jugador" + ((alive == 1) ? "" : "es"));
                strings.add("");
                strings.add("§3mc.groyland.net");
            }
            return strings;
        }
    }

