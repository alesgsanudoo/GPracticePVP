package me.groyteam.practice.commands.event;

import me.groyteam.practice.match.Match;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.util.Clickable;
import me.groyteam.practice.match.MatchTeam;
import java.util.UUID;
import me.groyteam.practice.tournament.Tournament;
import me.groyteam.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import me.groyteam.practice.Practice;
import org.bukkit.command.Command;

public class StatusEventCommand extends Command
{
    private final Practice plugin;
    
    public StatusEventCommand() {
        super("status");
        this.plugin = Practice.getInstance();
        this.setDescription("Enseña un evento");
        this.setUsage("§3§lArenaPvP §8» §fUso: §3/status§f.");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage("§3§lArenaPvP §8» §fNo puedes ir mientras estás en otros modos.");
            return true;
        }
        if (this.plugin.getTournamentManager().getTournaments().size() == 0) {
            player.sendMessage(ChatColor.RED + "There is no available tournaments.");
            return true;
        }
        for (final Tournament tournament : this.plugin.getTournamentManager().getTournaments().values()) {
            if (tournament == null) {
                player.sendMessage(ChatColor.RED + "This tournament doesn't exist.");
                return true;
            }
            player.sendMessage(" ");
            player.sendMessage("§3§lArenaPvP §8» §fTorneos disponibles: §3(" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + ") " + ChatColor.GOLD.toString() + tournament.getKitName());
            if (tournament.getMatches().size() == 0) {
                player.sendMessage("§3§lArenaPvP §8» §cNo hay torneos disponibles.");
                player.sendMessage(" ");
                return true;
            }
            for (final UUID matchUUID : tournament.getMatches()) {
                final Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);
                final MatchTeam teamA = match.getTeams().get(0);
                final MatchTeam teamB = match.getTeams().get(1);
                final String teamANames = (tournament.getTeamSize() > 1) ? (teamA.getLeaderName() + "Party") : teamA.getLeaderName();
                final String teamBNames = (tournament.getTeamSize() > 1) ? (teamB.getLeaderName() + "'Party") : teamB.getLeaderName();
                final Clickable clickable = new Clickable( ChatColor.DARK_AQUA.toString() + teamANames + " vs " + teamBNames + ChatColor.DARK_AQUA + " | " + ChatColor.GRAY + "(Click para espectear)", ChatColor.WHITE + "Click para espectear torneo.", "/spectate " + teamA.getLeaderName());
                clickable.sendToPlayer(player);
            }
            player.sendMessage(" ");
        }
        return true;
    }
}
