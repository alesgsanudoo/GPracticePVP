package net.latinplay.practice.commands;

import net.latinplay.practice.player.PlayerData;
import org.bukkit.entity.Player;
import net.latinplay.practice.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import java.util.List;
import java.util.Arrays;
import net.latinplay.practice.Practice;
import net.latinplay.practice.cache.MenuListener;
import net.latinplay.practice.cache.StatsMenu;
import org.bukkit.Statistic;
import org.bukkit.command.Command;

public class StatsCommand extends Command
{
    private final Practice plugin;
    
    public StatsCommand() {
        super("stats");
        this.plugin = Practice.getInstance();
        this.setAliases(Arrays.asList("elo", "statistics"));
        this.setUsage(ChatColor.RED + "Uso: /stats [player]");
    }
    
    @Override
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if(!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 0) {
            player.openInventory(MenuListener.getPlayerMenu(player, "stats").getInventory());
            return true;
        }
        Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        player.openInventory(MenuListener.getPlayerMenu(target, "stats").getInventory());
        return true;
    }
}
