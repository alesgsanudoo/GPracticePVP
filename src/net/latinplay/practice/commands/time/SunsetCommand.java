package net.latinplay.practice.commands.time;

import net.latinplay.practice.player.PlayerData;
import net.latinplay.practice.settings.item.ProfileOptionsItemState;
import net.latinplay.practice.Practice;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;

public class SunsetCommand extends Command
{
    public SunsetCommand() {
        super("sunset");
        this.setDescription("Poner de amanecer.");
        this.setUsage(ChatColor.RED + "§3§lArenaPvP §8» §fUsage: §3/sunset§f.");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        ((Player)sender).setPlayerTime(12000L, true);
        final PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(((Player)sender).getUniqueId());
        playerData.getOptions().setTime(ProfileOptionsItemState.SUNSET);
        return true;
    }
}
