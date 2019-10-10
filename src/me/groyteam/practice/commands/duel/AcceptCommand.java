package me.groyteam.practice.commands.duel;

import me.groyteam.practice.match.MatchTeam;
import me.groyteam.practice.party.Party;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.player.PlayerState;
import me.groyteam.practice.queue.QueueType;
import me.groyteam.practice.managers.PartyManager;
import java.util.List;
import me.groyteam.practice.kit.Kit;
import me.groyteam.practice.match.MatchRequest;
import me.groyteam.practice.match.Match;

import java.util.UUID;
import java.util.ArrayList;
import me.groyteam.practice.util.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import me.groyteam.practice.Practice;
import org.bukkit.command.Command;

public class AcceptCommand extends Command
{
    private final Practice plugin;
    
    public AcceptCommand() {
        super("accept");
        this.plugin = Practice.getInstance();
        this.setDescription("Acepta una duel de algun jugador.");
        this.setUsage("§3§lArenaPvP §8» §fUso: §3/accept (jugador)§f.");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (args.length < 1) {
            player.sendMessage(this.usageMessage);
            return true;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage("§3§lArenaPvP §8» §cNo se ha podido aceptar el duelo.");
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        if (player.getName().equals(target.getName())) {
            player.sendMessage("§3§lArenaPvP §8» §cEste jugador no existe.");
            return true;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage("§3§lArenaPvP §8» §cEste jugador ya esta en juego.");
            return true;
        }
        MatchRequest request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId());
        if (args.length > 1) {
            final Kit kit = this.plugin.getKitManager().getKit(args[1]);
            if (kit != null) {
                request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId(), kit.getName());
            }
        }
        if (request == null) {
            player.sendMessage("§3§lArenaPvP §8» §fNo hay peticiones §3pendientes§f.");
            return true;
        }
        if (request.getRequester().equals(target.getUniqueId())) {
            final List<UUID> playersA = new ArrayList<UUID>();
            final List<UUID> playersB = new ArrayList<UUID>();
            final PartyManager partyManager = this.plugin.getPartyManager();
            final Party party = partyManager.getParty(player.getUniqueId());
            final Party targetParty = partyManager.getParty(target.getUniqueId());
            if (request.isParty()) {
                if (party == null || targetParty == null || !partyManager.isLeader(target.getUniqueId()) || !partyManager.isLeader(target.getUniqueId())) {
                    player.sendMessage("§3§lArenaPvP §8» §cEse jugador no es el lider de esa party.");
                    return true;
                }
                playersA.addAll(party.getMembers());
                playersB.addAll(targetParty.getMembers());
            }
            else {
                if (party != null || targetParty != null) {
                    player.sendMessage("§3§lArenaPvP §8» §fEse §3jugador §fya está en una party.");
                    return true;
                }
                playersA.add(player.getUniqueId());
                playersB.add(target.getUniqueId());
            }
            final Kit kit2 = this.plugin.getKitManager().getKit(request.getKitName());
            final MatchTeam teamA = new MatchTeam(target.getUniqueId(), playersB, 0);
            final MatchTeam teamB = new MatchTeam(player.getUniqueId(), playersA, 1);
            final Match match = new Match(request.getArena(), kit2, QueueType.UNRANKED, new MatchTeam[] { teamA, teamB });
            final Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
            final Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());
            final String teamMatch = match.isPartyMatch() ? " Party" : "";
            match.broadcast("§3§¡COMENZANDO! §fComenzando la partida: " + ChatColor.DARK_AQUA + "(" + leaderA.getName() + teamMatch + " vs " + leaderB.getName() + teamMatch + ")§f.");
            this.plugin.getMatchManager().createMatch(match);
        }
        return true;
    }
}
