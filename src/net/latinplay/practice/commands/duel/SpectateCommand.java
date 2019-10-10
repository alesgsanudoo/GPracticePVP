package net.latinplay.practice.commands.duel;

import net.latinplay.practice.match.Match;
import net.latinplay.practice.events.PracticeEvent;
import net.latinplay.practice.party.Party;
import net.latinplay.practice.player.PlayerData;
import java.util.UUID;
import net.latinplay.practice.match.MatchTeam;
import net.latinplay.practice.events.redrover.RedroverEvent;
import net.latinplay.practice.events.parkour.ParkourEvent;
import net.latinplay.practice.events.oitc.OITCEvent;
import net.latinplay.practice.events.sumo.SumoEvent;
import net.latinplay.practice.util.StringUtil;
import net.latinplay.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Arrays;
import org.bukkit.ChatColor;
import net.latinplay.practice.Practice;
import org.bukkit.command.Command;

public class SpectateCommand extends Command
{
    private final Practice plugin;
    
    public SpectateCommand() {
        super("spectate");
        this.plugin = Practice.getInstance();
        this.setDescription("Espectar la partida del jugador.");
        this.setUsage("§3§lArenaPvP §8» §fUso: §3/spectate (jugador)§f.");
        this.setAliases(Arrays.asList("spec"));
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
        final Party party = this.plugin.getPartyManager().getParty(playerData.getUniqueId());
        if (party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
            player.sendMessage("§3§lArenaPvP §8» §cNo puedes espectar mientras estás en otros modos de juego.");
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        if (targetData.getPlayerState() == PlayerState.EVENT) {
            final PracticeEvent event = this.plugin.getEventManager().getEventPlaying(target);
            if (event == null) {
                player.sendMessage("§3§lArenaPvP §8» §fActualmente este jugador no esta en un evento.");
                return true;
            }
            if (event instanceof SumoEvent) {
                player.performCommand("eventspectate Sumo");
            }
            else if (event instanceof OITCEvent) {
                player.performCommand("eventspectate OITC");
            }
            else if (event instanceof ParkourEvent) {
                player.performCommand("eventspectate Parkour");
            }
            else if (event instanceof RedroverEvent) {
                player.performCommand("eventspectate Redrover");
            }
            return true;
        }
        else {
            if (targetData.getPlayerState() != PlayerState.FIGHTING) {
                player.sendMessage("§3§lArenaPvP §8» §cEste jugador no esta en partida.");
                return true;
            }
            final Match targetMatch = this.plugin.getMatchManager().getMatch(targetData);
            if (!targetMatch.isParty()) {
                if (!targetData.getOptions().isSpectators() && !player.hasPermission("practice.staff")) {
                    player.sendMessage("§3§lArenaPvP §8» §cEste jugador tiene la opción de espectador desactivada.");
                    return true;
                }
                final MatchTeam team = targetMatch.getTeams().get(0);
                final MatchTeam team2 = targetMatch.getTeams().get(1);
                final PlayerData otherPlayerData = this.plugin.getPlayerManager().getPlayerData((team.getPlayers().get(0) == target.getUniqueId()) ? team2.getPlayers().get(0) : team.getPlayers().get(0));
                if (otherPlayerData != null && !otherPlayerData.getOptions().isSpectators() && !player.hasPermission("practice.staff")) {
                    player.sendMessage("§3§lArenaPvP §8» §cEste jugador tiene la opción de espectador desactivada.");
                    return true;
                }
            }
            if (playerData.getPlayerState() == PlayerState.SPECTATING) {
                final Match match = this.plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());
                if (match.equals(targetMatch)) {
                    player.sendMessage("§3§lArenaPvP §8» §cYa estás espectando a este jugador.");
                    return true;
                }
                match.removeSpectator(player.getUniqueId());
            }
            player.sendMessage("§3§lArenaPvP §8» §fEstas espectando a " + ChatColor.DARK_AQUA + target.getName() + ChatColor.WHITE + ".");
            this.plugin.getMatchManager().addSpectator(player, playerData, target, targetMatch);
            return true;
        }
    }
}
