package net.latinplay.practice.managers;

import java.util.List;
import java.util.ArrayList;
import net.latinplay.practice.party.Party;
import net.latinplay.practice.arena.Arena;
import net.latinplay.practice.kit.Kit;
import net.latinplay.practice.match.Match;
import net.latinplay.practice.match.MatchTeam;
import java.util.Collections;
import org.bukkit.ChatColor;
import net.latinplay.practice.player.PlayerState;
import net.latinplay.practice.queue.QueueType;
import net.latinplay.practice.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.latinplay.practice.Practice;
import net.latinplay.practice.queue.QueueEntry;
import java.util.UUID;
import java.util.Map;
import java.util.logging.Level;

public class QueueManager
{
    private Map<UUID, QueueEntry> queued;
    private final Map<UUID, Long> playerQueueTime;
    private Practice plugin;
    private boolean rankedEnabled;

    public QueueManager() {
        this.queued = new ConcurrentHashMap<>();
        this.playerQueueTime = new HashMap<>();
        this.plugin = Practice.getInstance();
        this.rankedEnabled = true;
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)this.plugin, () -> this.queued.forEach((key, value) -> {
            this.findMatch(this.plugin.getServer().getPlayer(key), value.getKitName(), value.getElo(), value.getQueueType());
        }), 5L, 5L);
    }

    public void addPlayerToQueue(Player player, PlayerData playerData, String kitName, QueueType type) {
        if (type != QueueType.UNRANKED && !this.rankedEnabled) {
            player.closeInventory();
            return;
        }
        playerData.setPlayerState(PlayerState.QUEUE);
        int elo = (type == QueueType.RANKED) ? playerData.getElo(kitName) : 0;
        QueueEntry entry = new QueueEntry(type, kitName, elo, false);
        this.queued.put(playerData.getUniqueId(), entry);
        this.giveQueueItems(player);
        String unrankedMessage = ChatColor.YELLOW + "§3§lArenaPvP §8» §fBuscando partida con el kit §a§l" + kitName + "§f en §3§lModo UnRanked§f.";
        String rankedMessage = ChatColor.YELLOW + "§3§lArenaPvP §8» §fBuscando partida con el kit §a§l" + kitName  + "§f en §3§lModo Ranked§f. " + "" + playerData.getRankFromElo() + "";
        player.sendMessage((type == QueueType.UNRANKED) ? unrankedMessage : rankedMessage);
        this.playerQueueTime.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private void giveQueueItems(Player player) {
        player.closeInventory();
        player.getInventory().setContents(this.plugin.getItemManager().getQueueItems());
        player.updateInventory();
    }

    public QueueEntry getQueueEntry(UUID uuid) {
        return this.queued.get(uuid);
    }

    public long getPlayerQueueTime(UUID uuid) {
        return this.playerQueueTime.get(uuid);
    }

    public int getQueueSize(String ladder, QueueType type) {
        return (int)this.queued.entrySet().stream().filter(entry -> entry.getValue().getQueueType() == type).filter(entry -> entry.getValue().getKitName().equals(ladder)).count();
    }

    private boolean findMatch(Player player, String kitName, int elo, QueueType type) {
        long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(player.getUniqueId());
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().log(Level.WARNING, "{0}''s player data is null", String.valueOf(player.getName()));
            return false;
        }
        int eloRange = playerData.getEloRange();
        int pingRange = -1;
        int seconds = Math.round(queueTime / 1000L);
        if (seconds > 5 && type != QueueType.UNRANKED) {
            if (pingRange != -1) {
                pingRange += (seconds - 5) * 25;
            }
            if (eloRange != -1) {
                eloRange += seconds * 50;
                if (eloRange >= 3000) {
                    eloRange = 3000;
                }
            }
        }
        if (eloRange == -1) {
            eloRange = Integer.MAX_VALUE;
        }
        if (pingRange == -1) {
            pingRange = Integer.MAX_VALUE;
        }
        int ping = 0;
        for (UUID opponent : this.queued.keySet()) {
            if (opponent == player.getUniqueId()) {
                continue;
            }
            QueueEntry queueEntry = this.queued.get(opponent);
            if (!queueEntry.getKitName().equals(kitName)) {
                continue;
            }
            if (queueEntry.getQueueType() != type) {
                continue;
            }
            if (queueEntry.isParty()) {
                continue;
            }
            Player opponentPlayer = this.plugin.getServer().getPlayer(opponent);
            PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponent);
            if (opponentData.getPlayerState() == PlayerState.FIGHTING) {
                continue;
            }
            if (playerData.getPlayerState() == PlayerState.FIGHTING) {
                continue;
            }
            int eloDiff = Math.abs(queueEntry.getElo() - elo);
            if (type.isRanked()) {
                if (eloDiff > eloRange) {
                    continue;
                }
                long opponentQueueTime = System.currentTimeMillis() - this.playerQueueTime.get(opponentPlayer.getUniqueId());
                int opponentEloRange = -1;
                int opponentPingRange = -1;
                int opponentSeconds = Math.round(opponentQueueTime / 1000L);
                if (opponentSeconds > 5) {
                    if (opponentPingRange != -1) {
                        opponentPingRange += (opponentSeconds - 5) * 25;
                    }
                    if (opponentEloRange != -1) {
                        opponentEloRange += opponentSeconds * 50;
                        if (opponentEloRange >= 3000) {
                            opponentEloRange = 3000;
                        }
                    }
                }
                if (opponentEloRange == -1) {
                    opponentEloRange = Integer.MAX_VALUE;
                }
                if (opponentPingRange == -1) {
                    opponentPingRange = Integer.MAX_VALUE;
                }
                if (eloDiff > opponentEloRange) {
                    continue;
                }
                int pingDiff = Math.abs(0 - ping);
                if (type == QueueType.RANKED) {
                    if (pingDiff > opponentPingRange) {
                        continue;
                    }
                    if (pingDiff > pingRange) {
                        continue;
                    }
                }
            }
            Kit kit = this.plugin.getKitManager().getKit(kitName);
            Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
            String playerFoundMatchMessage;
            String matchedFoundMatchMessage;
            if (type.isRanked()) {
                playerFoundMatchMessage = ChatColor.DARK_AQUA + player.getName() + " §8(§b"+ this.queued.get(player.getUniqueId()).getElo() + " elo§8)";
                matchedFoundMatchMessage = ChatColor.DARK_AQUA + opponentPlayer.getName() + " §8(§b"+ this.queued.get(opponentPlayer.getUniqueId()).getElo() + " elo§8)";
            }
            else {
                playerFoundMatchMessage = ChatColor.GREEN + player.getName() + ".";
                matchedFoundMatchMessage = ChatColor.GREEN + opponentPlayer.getName() + ".";
            }
            player.sendMessage(ChatColor.YELLOW + "§3§l¡COMENZANDO! §fVa a comenzar el duelo contra: " + matchedFoundMatchMessage);
            opponentPlayer.sendMessage(ChatColor.YELLOW + "§3§l¡COMENZANDO! §fcomenzar el duelo contra: " + playerFoundMatchMessage);
            MatchTeam teamA = new MatchTeam(player.getUniqueId(), Collections.<UUID>singletonList(player.getUniqueId()), 0);
            MatchTeam teamB = new MatchTeam(opponentPlayer.getUniqueId(), Collections.<UUID>singletonList(opponentPlayer.getUniqueId()), 1);
            Match match = new Match(arena, kit, type, new MatchTeam[] { teamA, teamB });
            this.plugin.getMatchManager().createMatch(match);
            this.queued.remove(player.getUniqueId());
            this.queued.remove(opponentPlayer.getUniqueId());
            this.playerQueueTime.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    public void removePlayerFromQueue(Player player) {
        QueueEntry entry = this.queued.get(player.getUniqueId());
        this.queued.remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }

    private void findMatch(Party partyA, String kitName, int elo, QueueType type) {
        if (!this.playerQueueTime.containsKey(partyA.getLeader())) {
            return;
        }
        long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(partyA.getLeader());
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(partyA.getLeader());
        if (playerData == null) {
            return;
        }
        int eloRange = playerData.getEloRange();
        int seconds = Math.round(queueTime / 1000L);
        if (seconds > 5 && type.isRanked()) {
            eloRange += seconds * 50;
            if (eloRange >= 1000) {
                eloRange = 1000;
            }
        }
        int finalEloRange = eloRange;
        int n = elo;
        UUID opponent = this.queued.entrySet().stream().filter(entry -> entry.getKey() != partyA.getLeader()).filter(entry -> this.plugin.getPlayerManager().getPlayerData(entry.getKey()).getPlayerState() == PlayerState.QUEUE).filter(entry -> entry.getValue().isParty()).filter(entry -> entry.getValue().getQueueType() == type).filter(entry -> !type.isRanked() || Math.abs(entry.getValue().getElo() - elo) < n).filter(entry -> entry.getValue().getKitName().equals(kitName)).<UUID>map(Map.Entry::getKey).findFirst().orElse(null);
        if (opponent == null) {
            return;
        }
        PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponent);
        if (opponentData.getPlayerState() == PlayerState.FIGHTING) {
            return;
        }
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            return;
        }
        Player leaderA = this.plugin.getServer().getPlayer(partyA.getLeader());
        Player leaderB = this.plugin.getServer().getPlayer(opponent);
        Party partyB = this.plugin.getPartyManager().getParty(opponent);
        Kit kit = this.plugin.getKitManager().getKit(kitName);
        Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
        String partyAFoundMatchMessage;
        String partyBFoundMatchMessage;
        if (type.isRanked()) {
            partyAFoundMatchMessage = ChatColor.GREEN+ " §bParty de"+ leaderB.getName() + " §8(§"+ this.queued.get(leaderB.getUniqueId()).getElo() + " elo";
            partyBFoundMatchMessage = ChatColor.GREEN + leaderA.getName() + "'s Party" + ChatColor.YELLOW + " with " + ChatColor.GREEN + this.queued.get(leaderA.getUniqueId()).getElo() + " elo";
        }
        else {
            partyAFoundMatchMessage = ChatColor.GREEN + "§bParty de "+ leaderB.getName() +  ChatColor.WHITE + ".";
            partyBFoundMatchMessage = ChatColor.GREEN + "§bParty de "+ leaderA.getName() +  ChatColor.WHITE + ".";
        }
        partyA.broadcast(ChatColor.YELLOW + "§3§l¡COMENZANDO! §fComenzando duelo conta: " + partyAFoundMatchMessage);
        partyB.broadcast(ChatColor.YELLOW + "§3§l¡COMENZANDO! §fComenzando duelo contra: " + partyBFoundMatchMessage);
        List<UUID> playersA = new ArrayList<>(partyA.getMembers());
        List<UUID> playersB = new ArrayList<>(partyB.getMembers());
        MatchTeam teamA = new MatchTeam(leaderA.getUniqueId(), playersA, 0);
        MatchTeam teamB = new MatchTeam(leaderB.getUniqueId(), playersB, 1);
        Match match = new Match(arena, kit, type, new MatchTeam[] { teamA, teamB });
        this.plugin.getMatchManager().createMatch(match);
        this.queued.remove(partyA.getLeader());
        this.queued.remove(partyB.getLeader());
    }

    public void removePartyFromQueue(Party party) {
        QueueEntry entry = this.queued.get(party.getLeader());
        this.queued.remove(party.getLeader());
        party.members().forEach(this.plugin.getPlayerManager()::sendToSpawnAndReset);
        String type = entry.getQueueType().isRanked() ? "Ranked" : "Unranked";
        party.broadcast(String.valueOf(ChatColor.GREEN.toString()) + ChatColor.BOLD + "[*] " + ChatColor.YELLOW + "Tu fiesta ha dejado el " + type + " " + entry.getKitName() + ".");
    }

    public boolean isRankedEnabled() {
        return this.rankedEnabled;
    }

    public void setRankedEnabled(boolean rankedEnabled) {
        this.rankedEnabled = rankedEnabled;
    }
}
