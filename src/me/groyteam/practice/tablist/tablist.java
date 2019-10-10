package me.groyteam.practice.tablist;


import java.io.*;

import me.groyteam.practice.Practice;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;

public class tablist implements Listener
{
    private Practice plugin;
    public static FileConfiguration storage;
    public static File storageFile;

    public tablist(final Practice plugin) {
        this.plugin = plugin;
    }

    public tablist(final Player player, final String header, final String footer) {
    }

    @EventHandler
    public void onTablist(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        if (Practice.getInstance().getConfig().getBoolean("Tablist.Enable")) {
            this.plugin.updateTab(p);
            final String servername = Practice.getInstance().getConfig().getString("ServerName");
            final String ServerIP = Practice.getInstance().getConfig().getString("ServerIP");
            final int online1 = this.plugin.getServer().getOnlinePlayers().size();
            final String online2 = Integer.toString(online1);
            final int max = this.plugin.getServer().getMaxPlayers();
            final String max2 = Integer.toString(max);
            tablistAPI.sendPacket(e.getPlayer(), p);
        }
    }


    public static String tk(final String a) {
        return a.replaceAll("&", "§");
    }

    public static void onTablist() {
    }

    static {
        tablist.storage = null;
        tablist.storageFile = null;
    }
}
