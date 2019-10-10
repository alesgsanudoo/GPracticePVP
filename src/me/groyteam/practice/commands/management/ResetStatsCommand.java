package me.groyteam.practice.commands.management;

import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.kit.Kit;
import me.groyteam.practice.util.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import me.groyteam.practice.Practice;
import org.bukkit.command.Command;

public class ResetStatsCommand extends Command
{
    private final Practice plugin;
    
    public ResetStatsCommand() {
        super("reset");
        this.plugin = Practice.getInstance();
        this.setUsage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3/reset (nombre)§f.");
    }
    
    @Override
    public boolean execute(final CommandSender commandSender, final String s, final String[] args) {
        if (commandSender instanceof Player) {
            final Player player = (Player)commandSender;
            if (!player.hasPermission("practice.admin")) {
                player.sendMessage(ChatColor.RED + "Comando desconocido.");
                return true;
            }
        }
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3/reset (nombre)§f.");
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            commandSender.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        for (final Kit kit : this.plugin.getKitManager().getKits()) {
            playerData.setElo(kit.getName(), 800);
            playerData.setLosses(kit.getName(), 0);
            playerData.setWins(kit.getName(), 0);
        }
        commandSender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §fLas estadisticas de " + target.getName() + " han sido reseteadas.");
        return true;
    }
}
