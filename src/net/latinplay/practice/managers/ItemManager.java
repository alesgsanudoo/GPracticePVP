package net.latinplay.practice.managers;

import net.latinplay.practice.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemManager
{
    private final ItemStack[] spawnItems;
    private final ItemStack[] queueItems;
    private final ItemStack[] partyItems;
    private final ItemStack[] tournamentItems;
    private final ItemStack[] eventItems;
    private final ItemStack[] specItems;
    private final ItemStack[] partySpecItems;
    private final ItemStack defaultBook;
    
    public ItemManager() {
        final ItemStack[] spawnItems = new ItemStack[9];
        spawnItems[1] = ItemUtil.createItem(Material.SLIME_BALL, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD +"Menu de ArenaPvP §a(Click Derecho)");
        spawnItems[2] = ItemUtil.createItem(Material.NAME_TAG, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Crear Party §a(Click Derecho)");
        spawnItems[0] = ItemUtil.createItem(Material.COMPASS, ChatColor.YELLOW.toString() + ChatColor.BOLD + "§fSelector de Modalidades §a(Click Derecho)");
        spawnItems[7] = ItemUtil.createItem(Material.BOOK, ChatColor.YELLOW.toString() + ChatColor.BOLD + "§fOpciones de Usuario §a(Click Derecho)");
        spawnItems[8] = ItemUtil.createItem(Material.GOLD_INGOT, ChatColor.YELLOW.toString() + ChatColor.BOLD + "§fTienda Oficial §a(Click Derecho)");
        this.spawnItems = spawnItems;
        this.queueItems = new ItemStack[] { null, null, null, null, ItemUtil.createItem(Material.REDSTONE, ChatColor.RED.toString()+ ChatColor.BOLD + "Salir §a(Click Derecho)"), null, null, null, null };
        this.specItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString()+ ChatColor.BOLD + "Salir del modo Espectador §a(Click Derecho)") };
        this.partySpecItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.SUGAR, ChatColor.RED.toString()+ ChatColor.BOLD + "Salir de la Party §a(Click Derecho)") };
        this.tournamentItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString()+ ChatColor.BOLD + "Salir del Torneo §a(Click Derecho)") };
        this.eventItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString()+ ChatColor.BOLD + "Salir del evento §a(Click Derecho)") };
        this.partyItems = new ItemStack[] { ItemUtil.createItem(Material.DIAMOND_AXE, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Luchar contra otras parties §a(Click Derecho)"), ItemUtil.createItem(Material.REDSTONE_TORCH_ON, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Información de la Party §a(Click Derecho)"),null ,null ,null , null, null,null ,ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + ChatColor.BOLD + "Salir de la Party §a(Click Derecho)")};
        this.defaultBook = ItemUtil.createItem(Material.SNOW_BALL, ChatColor.YELLOW.toString() + "Kit Predeterminado");
    }
    
    public ItemStack[] getSpawnItems() {
        return this.spawnItems;
    }
    
    public ItemStack[] getQueueItems() {
        return this.queueItems;
    }
    
    public ItemStack[] getPartyItems() {
        return this.partyItems;
    }
    
    public ItemStack[] getTournamentItems() {
        return this.tournamentItems;
    }
    
    public ItemStack[] getEventItems() {
        return this.eventItems;
    }
    
    public ItemStack[] getSpecItems() {
        return this.specItems;
    }
    
    public ItemStack[] getPartySpecItems() {
        return this.partySpecItems;
    }
    
    public ItemStack getDefaultBook() {
        return this.defaultBook;
    }
}
