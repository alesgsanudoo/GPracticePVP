package net.latinplay.practice.commands.time;

import net.latinplay.practice.player.PlayerData;
import net.latinplay.practice.settings.item.ProfileOptionsItemState;
import net.latinplay.practice.Practice;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;

public class DayCommand extends Command
{
    public DayCommand() {
        super("day");
        this.setDescription("Hacer de dia.");
        this.setUsage(ChatColor.RED + "§3§lArenaPvP §8» §fUsage: §3/day§f.");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        ((Player)sender).setPlayerTime(6000L, true);
        final PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(((Player)sender).getUniqueId());
        playerData.getOptions().setTime(ProfileOptionsItemState.DAY);
        return true;
    }
}
