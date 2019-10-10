package net.latinplay.practice.runnable;

import net.latinplay.practice.player.PlayerData;
import net.latinplay.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SaveDataRunnable implements Runnable
{
    private final Practice plugin;

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            this.plugin.getPlayerManager().saveData(playerData);
        }
        for (final PlayerData playerData : this.plugin.getPlayerManager().getAllData()) {
            this.plugin.getPlayerManager().saveData(playerData);
        }
    }

    public SaveDataRunnable() {
        this.plugin = Practice.getInstance();
    }
}
