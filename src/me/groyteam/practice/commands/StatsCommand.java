package me.groyteam.practice.commands;

import org.bukkit.entity.Player;
import me.groyteam.practice.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import java.util.Arrays;
import me.groyteam.practice.Practice;
import me.groyteam.practice.cache.MenuListener;
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
