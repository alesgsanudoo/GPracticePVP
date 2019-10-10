package com.bizarrealex.aether.example;

import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.block.Action;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Iterator;
import com.bizarrealex.aether.scoreboard.cooldown.BoardFormat;
import java.util.ArrayList;
import java.util.List;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import java.util.Set;
import com.bizarrealex.aether.scoreboard.Board;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import com.bizarrealex.aether.scoreboard.BoardAdapter;

public class ExampleBoardAdapter implements BoardAdapter, Listener
{
    public ExampleBoardAdapter(final JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    @Override
    public String getTitle(final Player player) {
        return "&6&lArena&e&lPvP";
    }
    
    @Override
    public List<String> getScoreboard(final Player player, final Board board, final Set<BoardCooldown> cooldowns) {
        final List<String> strings = new ArrayList<>();
        strings.add("&7&m-------------------");
        for (final BoardCooldown cooldown : cooldowns) {
            if (cooldown.getId().equals("enderpearl")) {
                strings.add("&fEnderpearl&7:&e " + cooldown.getFormattedString(BoardFormat.SECONDS));
            }
        }
        strings.add("&7&m-------------------&r");
        if (strings.size() == 2) {
            return null;
        }
        return strings;
    }
    
    @EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Board board = Board.getByPlayer(player);
        if (event.getAction().name().contains("RIGHT")) {
            if (event.getItem() == null) {
                return;
            }
            if (event.getItem().getType() != Material.ENDER_PEARL) {
                return;
            }
            if (board == null) {
                return;
            }
            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                return;
            }
            final BoardCooldown cooldown = board.getCooldown("enderpearl");
            if (cooldown != null) {
                event.setCancelled(true);
                player.updateInventory();
                player.sendMessage(ChatColor.RED + "Tu debes esperar " + cooldown.getFormattedString(BoardFormat.SECONDS) + " segundos para usar otra enderpearl!");
                return;
            }
            new BoardCooldown(board, "enderpearl", 16.0);
        }
    }
}
