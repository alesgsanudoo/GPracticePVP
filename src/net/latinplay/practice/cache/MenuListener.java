package net.latinplay.practice.cache;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class MenuListener implements Listener
{
    public static HashMap<String, HashMap<String, Menu>> menus;
    
    public static HashMap<String, Menu> getPlayerMenus(Player player) {
        if (MenuListener.menus.containsKey(player.getName())) {
            return MenuListener.menus.get(player.getName());
        }
        return new HashMap<>();
    }
    
    public static Menu getPlayerMenu(Player player, String s) {
        if (getPlayerMenus(player).containsKey(s)) {
            return getPlayerMenus(player).get(s);
        }
        return null;
    }
    
    @EventHandler
    public void onPlayerLeaveInvRemove(PlayerQuitEvent playerQuitEvent) {
        MenuListener.menus.remove(playerQuitEvent.getPlayer().getName());
    }
    
    @EventHandler
    public void onPlayerKickInvRemove(PlayerKickEvent playerKickEvent) {
        MenuListener.menus.remove(playerKickEvent.getPlayer().getName());
    }
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent inventoryOpenEvent) {
        for (Menu menu : getPlayerMenus((Player)inventoryOpenEvent.getPlayer()).values()) {
            if (inventoryOpenEvent.getInventory().getTitle().equals(menu.getInventory().getTitle())) {
                menu.onOpen(inventoryOpenEvent);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        for (Menu menu : getPlayerMenus((Player)inventoryCloseEvent.getPlayer()).values()) {
            if (inventoryCloseEvent.getInventory().getTitle().equals(menu.getInventory().getTitle())) {
                menu.onClose(inventoryCloseEvent);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        for (Menu menu : getPlayerMenus((Player)inventoryClickEvent.getWhoClicked()).values()) {
            if (inventoryClickEvent.getInventory().getTitle().equals(menu.getInventory().getTitle()) && inventoryClickEvent.getCurrentItem() != null) {
                inventoryClickEvent.setCancelled(true);
                menu.onClick(inventoryClickEvent);
            }
        }
    }
    
    static {
        MenuListener.menus = new HashMap<>();
    }
}
