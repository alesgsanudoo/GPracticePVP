package me.groyteam.practice.commands.event;

import me.groyteam.practice.events.PracticeEvent;
import me.groyteam.practice.party.Party;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.events.redrover.RedroverEvent;
import me.groyteam.practice.events.parkour.ParkourEvent;
import me.groyteam.practice.events.oitc.OITCEvent;
import me.groyteam.practice.events.sumo.SumoEvent;
import me.groyteam.practice.events.EventState;
import me.groyteam.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import org.bukkit.ChatColor;
import me.groyteam.practice.Practice;
import org.bukkit.command.Command;

public class SpectateEventCommand extends Command
{
    private final Practice plugin;
    
    public SpectateEventCommand() {
        super("eventspectate");
        this.plugin = Practice.getInstance();
        this.setDescription("Espectear un evento.");
        this.setUsage("§3§lArenaPvP §8» §fUso: §3/eventspectate (Evento)§f.");
        this.setAliases(Arrays.asList("espec", "specevent"));
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
        final PracticeEvent event = this.plugin.getEventManager().getByName(args[0]);
        if (event == null) {
            player.sendMessage("§3§lArenaPvP §8» §fUso: §3/leave§f");
            return true;
        }
        if (event.getState() != EventState.STARTED) {
            player.sendMessage("§3§lArenaPvP §8» §fEl evento todavía no ha empezado, por favor espera.");
            return true;
        }
        if (playerData.getPlayerState() == PlayerState.SPECTATING) {
            if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                player.sendMessage("§3§lArenaPvP §8» §cYa estás espectando este evento.");
                return true;
            }
            this.plugin.getEventManager().removeSpectator(player);
        }
        player.sendMessage("§3§lArenaPvP §8» §fEstas espectando el evento: " + ChatColor.DARK_AQUA + event.getName() + ChatColor.WHITE + ".");
        if (event instanceof SumoEvent) {
            this.plugin.getEventManager().addSpectatorSumo(player, playerData, (SumoEvent)event);
        }
        else if (event instanceof OITCEvent) {
            this.plugin.getEventManager().addSpectatorOITC(player, playerData, (OITCEvent)event);
        }
        else if (event instanceof ParkourEvent) {
            this.plugin.getEventManager().addSpectatorParkour(player, playerData, (ParkourEvent)event);
        }
        else if (event instanceof RedroverEvent) {
            this.plugin.getEventManager().addSpectatorRedrover(player, playerData, (RedroverEvent)event);
        }
        return true;
    }
}
