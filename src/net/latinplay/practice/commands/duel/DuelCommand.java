package net.latinplay.practice.commands.duel;

import net.latinplay.practice.party.Party;
import net.latinplay.practice.player.PlayerData;
import net.latinplay.practice.util.StringUtil;
import net.latinplay.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import net.latinplay.practice.Practice;
import org.bukkit.command.Command;

public class DuelCommand extends Command
{
    private final Practice plugin;
    
    public DuelCommand() {
        super("duel");
        this.plugin = Practice.getInstance();
        this.setDescription("Enviar duelo a un jugador.");
        this.setUsage("§3§lArenaPvP §8» §fUso: §3/duel (jugador)§f.");
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
        if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
            player.sendMessage("§3§lArenaPvP §8» §fActualmente estás en un §3torneo§f.");
            return true;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage("§3§lArenaPvP §8» §fNo se puede enviar duelos mientras estás en otros modos de juego§f.");
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        if (this.plugin.getTournamentManager().getTournament(target.getUniqueId()) != null) {
            player.sendMessage("§3§lArenaPvP §8» §fActualmente este §3jugador §festa en un torneo.");
            return true;
        }
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
        if (player.getName().equals(target.getName())) {
            player.sendMessage("§3§lArenaPvP §8» §cNo te puedes retar ti mismo.");
            return true;
        }
        if (party != null && targetParty != null && party == targetParty) {
            player.sendMessage("§3§lArenaPvP §8» §cNo puedes retar a un miembro de tu part.");
            return true;
        }
        if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            player.sendMessage("§3§lArenaPvP §8» §cNo eres el lider de la party.");
            return true;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage("§3§lArenaPvP §8» §cEste jugador ya esta en juego.");
            return true;
        }
        if (!targetData.getOptions().isDuelRequests()) {
            player.sendMessage("§3§lArenaPvP §8» §cEl jugador que has retado, ha denegado la petición.");
            return true;
        }
        if (party == null && targetParty != null) {
            player.sendMessage("§3§lArenaPvP §8» §cEse jugador ya está en una party.");
            return true;
        }
        if (party != null && targetParty == null) {
            player.sendMessage("§3§lArenaPvP §8» §cActualmente estás en una party.");
            return true;
        }
        playerData.setDuelSelecting(target.getUniqueId());
        player.openInventory(this.plugin.getInventoryManager().getDuelInventory().getCurrentPage());
        return true;
    }
}
