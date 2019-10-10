package me.groyteam.practice.cache;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class Menu
{
    private String menuId;
    private Inventory inv;
    private String player;
    private String back;
    
    public Menu(Player player, String menuId, String s, int n) {
        this.player = player.getName();
        this.menuId = menuId;
        this.inv = Bukkit.createInventory(null, n * 9, ChatColor.translateAlternateColorCodes('&', s));
        this.setBack("none");
        HashMap<String, Menu> playerMenus = MenuListener.getPlayerMenus(player);
        playerMenus.put(menuId, this);
        MenuListener.menus.put(player.getName(), playerMenus);
    }
    
    public Menu(Player player, String menuId, String s, int n, String back) {
        this.player = player.getName();
        this.menuId = menuId;
        this.inv = Bukkit.createInventory(null, n * 9, ChatColor.translateAlternateColorCodes('&', s));
        this.setBack(back);
        HashMap<String, Menu> playerMenus = MenuListener.getPlayerMenus(player);
        playerMenus.put(menuId, this);
        MenuListener.menus.put(player.getName(), playerMenus);
    }
    
    public Menu addItem(ItemStack itemStack) {
        this.inv.addItem(itemStack);
        return this;
    }
    
    public Menu addItem(ItemBuilder2 itemBuilder) {
        return this.addItem(itemBuilder.build());
    }
    
    public Menu setItem(int n, ItemBuilder2 itemBuilder) {
        this.inv.setItem(n, itemBuilder.build());
        return this;
    }
    
    public Menu setItem(int n, int n2, ItemBuilder2 itemBuilder) {
        this.inv.setItem((n - 1) * 9 + (n2 - 1), itemBuilder.build());
        return this;
    }
    
    public Menu setItem(int n, int n2, ItemStack itemStack) {
        this.inv.setItem(n * 9 + n2, itemStack);
        return this;
    }
    
    public Inventory getInventory() {
        return this.inv;
    }
    
    public void newInventoryName(String s) {
        this.inv = Bukkit.createInventory(null, this.inv.getSize(), s);
    }
    
    public String getMenuId() {
        return this.menuId;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(this.player);
    }
    
    public String getBack() {
        return this.back;
    }
    
    public void setBack(String back) {
        this.back = back;
    }
    
    public void addFullLine(int n, ItemBuilder2 itemBuilder) {
        itemBuilder.setTitle(" &r");
        for (int i = 1; i < 10; ++i) {
            this.setItem(n, i, itemBuilder);
        }
    }
    
    public abstract void onOpen(InventoryOpenEvent p0);
    
    public abstract void onClose(InventoryCloseEvent p0);
    
    public abstract void onClick(InventoryClickEvent p0);
    
    public abstract void update();
}
