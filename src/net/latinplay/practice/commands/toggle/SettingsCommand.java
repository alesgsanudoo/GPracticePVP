package net.latinplay.practice.commands.toggle;

import net.latinplay.practice.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Arrays;
import org.bukkit.ChatColor;
import net.latinplay.practice.Practice;
import org.bukkit.command.Command;

public class SettingsCommand extends Command
{
    private final Practice plugin;

    public SettingsCommand() {
        super("settings");
        this.plugin = Practice.getInstance();
        this.setDescription("Gestionar tus opciones.");
        this.setUsage(ChatColor.RED + "§3§lArenaPvP §8» §fUsage: §3/settings§f.");
        this.setAliases(Arrays.asList("options", "toggle"));
    }

    @Override
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        player.openInventory(playerData.getOptions().getInventory());
        return true;
    }
}
