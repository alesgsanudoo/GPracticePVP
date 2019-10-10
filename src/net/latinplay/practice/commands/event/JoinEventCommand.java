package net.latinplay.practice.commands.event;

import net.latinplay.practice.party.Party;
import net.latinplay.practice.tournament.Tournament;
import net.latinplay.practice.events.PracticeEvent;
import net.latinplay.practice.player.PlayerData;
import net.latinplay.practice.tournament.TournamentState;
import net.latinplay.practice.events.EventState;
import org.apache.commons.lang.math.NumberUtils;
import net.latinplay.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import net.latinplay.practice.Practice;
import org.bukkit.command.Command;

public class JoinEventCommand extends Command
{
    private final Practice plugin;
    
    public JoinEventCommand() {
        super("join");
        this.plugin = Practice.getInstance();
        this.setDescription("Entrar en un evento.");
        this.setUsage("§3§lArenaPvP §8» §fUso: §3/join (evento)§f.");
    }
    
    @Override
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
        if (this.plugin.getPartyManager().getParty(playerData.getUniqueId()) != null || playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage("§3§lArenaPvP §8» §fNo puedes ir mientras estás en otras cosas§f.");
            return true;
        }
        final boolean inTournament = this.plugin.getTournamentManager().isInTournament(player.getUniqueId());
        final boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;
        final String eventId = args[0].toLowerCase();
        if (!NumberUtils.isNumber(eventId)) {
            final PracticeEvent event = this.plugin.getEventManager().getByName(eventId);
            if (inTournament) {
                player.sendMessage("§3§lArenaPvP §8» §fNo puedes ir mientras estás en otros modos de juego§f.");
                return true;
            }
            if (event == null) {
                player.sendMessage("§3§lArenaPvP §8» §fEste evento no existe.");
                return true;
            }
            if (event.getState() != EventState.WAITING) {
                player.sendMessage("§3§lArenaPvP §8» §cNo hay eventos disponibles");
                return true;
            }
            if (event.getPlayers().containsKey(player.getUniqueId())) {
                player.sendMessage("§3§lArenaPvP §8» §cYa estás en un evento.");
                return true;
            }
            if (event.getPlayers().size() >= event.getLimit() && !player.hasPermission("practice.joinevent.bypass")) {
                player.sendMessage("§3§lArenaPvP §8» §cEl evento ya no esta lleno.");
            }
            event.join(player);
            return true;
        }
        else {
            if (inEvent) {
                player.sendMessage("§3§lArenaPvP §8» §fNo puedes ir mientras estás en otros modos de juego§f.");
                return true;
            }
            if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                player.sendMessage("§3§lArenaPvP §8» §cActualmente estás en un torneo.");
                return true;
            }
            final int id = Integer.parseInt(eventId);
            final Tournament tournament = this.plugin.getTournamentManager().getTournament(Integer.valueOf(id));
            if (tournament != null) {
                if (tournament.getTeamSize() > 1) {
                    final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
                    if (party != null && party.getMembers().size() != tournament.getTeamSize()) {
                        player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fEl máximo de la party debe ser de §3" + tournament.getTeamSize() + " §fjugadores.");
                        return true;
                    }
                }
                if (tournament.getSize() > tournament.getPlayers().size()) {
                    if ((tournament.getTournamentState() == TournamentState.WAITING || tournament.getTournamentState() == TournamentState.STARTING) && tournament.getCurrentRound() == 1) {
                        this.plugin.getTournamentManager().joinTournament(id, player);
                    }
                    else {
                        player.sendMessage("§3§lArenaPvP §8» §fLo siento, el torneo ya ha empezado.");
                    }
                }
                else {
                    player.sendMessage("§3§lArenaPvP §8» §fLo siento, el torneo ya esta lleno.");
                }
            }
            else {
                player.sendMessage("§3§lArenaPvP §8» §fEste torneo no existe.");
            }
            return true;
        }
    }
}
