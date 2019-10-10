package me.groyteam.practice.commands.warp;

import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import me.groyteam.practice.Practice;
import org.bukkit.command.Command;

public class WarpCommand extends Command
{
    private final Practice plugin;
    
    public WarpCommand() {
        super("spawn");
        this.plugin = Practice.getInstance();
        this.setDescription("Spawn command.");
        this.setUsage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3Uso: §3/setspawn (args)§f.");
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
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.FFA) {
            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo puedes hacer esto mientras estás en otros modos de juego.");
            return true;
        }
        if (args.length == 0) {
            this.plugin.getPlayerManager().sendToSpawnAndReset(player);
            return true;
        }
        return true;
    }
}
