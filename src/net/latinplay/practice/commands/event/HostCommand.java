package net.latinplay.practice.commands.event;

import net.latinplay.practice.events.PracticeEvent;
import net.latinplay.practice.util.TimeUtil;
import org.apache.commons.lang.math.NumberUtils;
import java.util.function.Consumer;
import net.latinplay.practice.util.Clickable;
import net.latinplay.practice.events.EventState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import net.latinplay.practice.Practice;
import net.latinplay.practice.cache.HostMenu;
import net.latinplay.practice.cache.MenuListener;
import org.bukkit.command.Command;

public class HostCommand extends Command
{
    private final Practice plugin;
    
    public HostCommand() {
        super("host");
        this.plugin = Practice.getInstance();
        this.setDescription("Hostear un evento.");
        this.setUsage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3/host (evento)§f.");
    }
    
    @Override
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.host")) {
            player.sendMessage("§3§lArenaPvP §8» §fNo tienes permisos, necesitas un rango §3§lSUPERIOR§f,compralo en §3tienda.groyland.net§f.");
            return true;
        }
        if (args.length < 1) {
            player.openInventory(MenuListener.getPlayerMenu(player, "host").getInventory());
            return true;
        }
        final String eventName = args[0];
        if (eventName == null) {
            return true;
        }
        if (this.plugin.getEventManager().getByName(eventName) == null) {
            player.sendMessage("§3§lArenaPvP §8» §fEste evento no existe.");
            player.sendMessage("§3§lArenaPvP §8» §fLo siento, pero por ahora solo contamos con: §3Sumo");
            return true;
        }
        if (eventName.toUpperCase().equalsIgnoreCase("REDROVER") || eventName.toUpperCase().equalsIgnoreCase("IOTIC") || eventName.toUpperCase().equalsIgnoreCase("PARKOUR")) {
            player.sendMessage("§3§lArenaPvP §8» §fEvento desactivado.");
            return true;
        }
        if (System.currentTimeMillis() < this.plugin.getEventManager().getCooldown()) {
            player.sendMessage("§3§lArenaPvP §8» §fHay que esperar §3" + TimeUtil.convertToFormat(this.plugin.getEventManager().getCooldown()) + "§fpara volver a comenzar un evento.");
            return true;
        }
        final PracticeEvent event = this.plugin.getEventManager().getByName(eventName);
        if (event.getState() != EventState.UNANNOUNCED) {
            player.sendMessage("§3§lArenaPvP §8» §cNo hay eventos activos.");
            return true;
        }
        final boolean eventBeingHosted = this.plugin.getEventManager().getEvents().values().stream().anyMatch(e -> e.getState() != EventState.UNANNOUNCED);
        if (eventBeingHosted) {
            player.sendMessage("§3§lArenaPvP §8» §fYa hay un evento en funcionamiento.");
            return true;
        }
        final String toSend = "§3§lArenaPvP §8» §fIniciado evento de §b" + event.getName() + "§f. §3(Click para entrar al evento).";
        final Clickable message = new Clickable(toSend, ChatColor.WHITE + "Click para entrar a este evento.", "/join " + event.getName());
        this.plugin.getServer().getOnlinePlayers().forEach(message::sendToPlayer);
        event.setLimit(55);
        if (args.length == 2 && player.hasPermission("practice.host.unlimited")) {
            if (!NumberUtils.isNumber(args[1])) {
                player.sendMessage("§3§lArenaPvP §8» §fEsa no es una cifra valida");
                return true;
            }
            event.setLimit(Integer.parseInt(args[1]));
        }
        Practice.getInstance().getEventManager().hostEvent(event, player);
        return true;
    }
}
