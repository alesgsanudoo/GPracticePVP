package net.latinplay.practice.commands.event;

import net.latinplay.practice.events.PracticeEvent;
import net.latinplay.practice.tournament.Tournament;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import net.latinplay.practice.Practice;
import org.bukkit.command.Command;

public class LeaveEventCommand extends Command
{
    private final Practice plugin;
    
    public LeaveEventCommand() {
        super("leave");
        this.plugin = Practice.getInstance();
        this.setDescription("Salir de un evento.");
        this.setUsage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3/leave§f.");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final boolean inTournament = this.plugin.getTournamentManager().isInTournament(player.getUniqueId());
        final boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;
        if (inEvent) {
            this.leaveEvent(player);
        }
        else if (inTournament) {
            this.leaveTournament(player);
        }
        else {
            player.sendMessage("§3§lArenaPvP §8» §fNo estás  en un evento.");
        }
        return true;
    }
    
    private void leaveTournament(final Player player) {
        final Tournament tournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId());
        if (tournament != null) {
            this.plugin.getTournamentManager().leaveTournament(player);
        }
    }
    
    private void leaveEvent(final Player player) {
        final PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);
        if (event == null) {
            player.sendMessage("§3§lArenaPvP §8» §fEste evento no existe.");
            return;
        }
        if (!this.plugin.getEventManager().isPlaying(player, event)) {
            player.sendMessage("§3§lArenaPvP §8» §fNo estás en un evento.");
            return;
        }
        event.leave(player);
    }
}
