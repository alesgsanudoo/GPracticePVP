package me.groyteam.practice.runnable;

import me.groyteam.practice.Practice;
import me.groyteam.practice.player.PlayerData;
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
