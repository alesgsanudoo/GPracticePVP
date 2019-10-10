package net.latinplay.practice.commands.event;

import net.latinplay.practice.events.PracticeEvent;
import net.latinplay.practice.events.EventState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import net.latinplay.practice.Practice;
import org.bukkit.command.Command;

public class EventManagerCommand extends Command
{
    private final Practice plugin;
    
    public EventManagerCommand() {
        super("eventmanager");
        this.plugin = Practice.getInstance();
        this.setDescription("Gestionar un evento.");
        this.setUsage("§3§lArenaPvP §8» §fUso: §3/eventmanager (start/end/status/cooldown) (event)§f.");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.admin")) {
            player.sendMessage(ChatColor.RED + "Comando desconocido.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(this.usageMessage);
            return true;
        }
        final String action = args[0];
        final String eventName = args[1];
        if (this.plugin.getEventManager().getByName(eventName) == null) {
            player.sendMessage("§3§lArenaPvP §8» §cEste evento no existe.");
            return true;
        }
        final PracticeEvent event = this.plugin.getEventManager().getByName(eventName);
        if (action.toUpperCase().equalsIgnoreCase("START") && event.getState() == EventState.WAITING) {
            event.getCountdownTask().setTimeUntilStart(5);
            player.sendMessage("§3§lArenaPvP §8» §fEl evento ha sido forzado a empezar.");
        }
        else if (action.toUpperCase().equalsIgnoreCase("END") && event.getState() == EventState.STARTED) {
            event.end();
            player.sendMessage("§3§lArenaPvP §8» §fEl evento ha sido cancelado.");
        }
        else if (action.toUpperCase().equalsIgnoreCase("STATUS")) {
            final String[] message = { ChatColor.YELLOW + "Event: " + ChatColor.WHITE + event.getName(), ChatColor.YELLOW + "Host: " + ChatColor.WHITE + ((event.getHost() == null) ? "Player Left" : event.getHost().getName()), ChatColor.YELLOW + "Players: " + ChatColor.WHITE + event.getPlayers().size() + "/" + event.getLimit(), ChatColor.YELLOW + "State: " + ChatColor.WHITE + event.getState().name() };
            player.sendMessage(message);
        }
        else if (action.toUpperCase().equalsIgnoreCase("COOLDOWN")) {
            this.plugin.getEventManager().setCooldown(0L);
            player.sendMessage("§3§lArenaPvP §8» §fSe ha cancelado el cooldown de la partida.");
        }
        else {
            player.sendMessage(this.usageMessage);
        }
        return true;
    }
}
