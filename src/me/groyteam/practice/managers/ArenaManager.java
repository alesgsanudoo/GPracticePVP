package me.groyteam.practice.managers;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;

import me.groyteam.practice.CustomLocation;
import me.groyteam.practice.Practice;
import me.groyteam.practice.arena.Arena;
import me.groyteam.practice.arena.StandaloneArena;
import me.groyteam.practice.file.Config;
import me.groyteam.practice.kit.Kit;
import me.groyteam.practice.util.ItemUtil;
import me.groyteam.practice.util.inventory.InventoryUI;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.Iterator;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class ArenaManager
{
    private final Practice plugin;
    private final Config config;
    private final Map<String, Arena> arenas;
    private final Map<StandaloneArena, UUID> arenaMatchUUIDs;
    private int generatingArenaRunnables;

    public ArenaManager() {
        this.plugin = Practice.getInstance();
        this.config = new Config("arenas", this.plugin);
        this.arenas = new HashMap<>();
        this.arenaMatchUUIDs = new HashMap<>();
        this.loadArenas();
    }

    private void loadArenas() {
        FileConfiguration fileConfig = this.config.getConfig();
        ConfigurationSection arenaSection = fileConfig.getConfigurationSection("arenas");
        if (arenaSection == null) {
            return;
        }
        arenaSection.getKeys(false).forEach(name -> {
            String a = arenaSection.getString(String.valueOf(name) + ".a");
            String b = arenaSection.getString(String.valueOf(name) + ".b");
            String min = arenaSection.getString(String.valueOf(name) + ".min");
            String max = arenaSection.getString(String.valueOf(name) + ".max");
            CustomLocation locA = CustomLocation.stringToLocation(a);
            CustomLocation locB = CustomLocation.stringToLocation(b);
            CustomLocation locMin = CustomLocation.stringToLocation(min);
            CustomLocation locMax = CustomLocation.stringToLocation(max);
            ArrayList<StandaloneArena> standaloneArenas = new ArrayList<>();
            ConfigurationSection saSection = arenaSection.getConfigurationSection(String.valueOf(name) + ".standaloneArenas");
            if (saSection != null) {
                saSection.getKeys(false).forEach(id -> {
                    String saA = saSection.getString(String.valueOf(id) + ".a");
                    String saB = saSection.getString(String.valueOf(id) + ".b");
                    String saMin = saSection.getString(String.valueOf(id) + ".min");
                    String saMax = saSection.getString(String.valueOf(id) + ".max");
                    CustomLocation locSaA = CustomLocation.stringToLocation(saA);
                    CustomLocation locSaB = CustomLocation.stringToLocation(saB);
                    CustomLocation locSaMin = CustomLocation.stringToLocation(saMin);
                    CustomLocation locSaMax = CustomLocation.stringToLocation(saMax);
                    standaloneArenas.add(new StandaloneArena(locSaA, locSaB, locSaMin, locSaMax));
                });
            }
            boolean enabled = arenaSection.getBoolean(String.valueOf(name) + ".enabled", false);
            Arena arena = new Arena((String)name, (List<StandaloneArena>)standaloneArenas, (List<StandaloneArena>)new ArrayList<StandaloneArena>(standaloneArenas), locA, locB, locMin, locMax, enabled);
            this.arenas.put((String)name, arena);
        });
    }

    public void saveArenas() {
        FileConfiguration fileConfig = this.config.getConfig();
        fileConfig.set("arenas", (Object)null);
        FileConfiguration fileConfiguration = fileConfig;
        this.arenas.forEach((arenaName, arena) -> {
            String a = CustomLocation.locationToString(arena.getA());
            String b = CustomLocation.locationToString(arena.getB());
            String min = CustomLocation.locationToString(arena.getMin());
            String max = CustomLocation.locationToString(arena.getMax());
            String arenaRoot = "arenas." + arenaName;
            fileConfiguration.set(String.valueOf(arenaRoot) + ".a", (Object)a);
            fileConfiguration.set(String.valueOf(arenaRoot) + ".b", (Object)b);
            fileConfiguration.set(String.valueOf(arenaRoot) + ".min", (Object)min);
            fileConfiguration.set(String.valueOf(arenaRoot) + ".max", (Object)max);
            fileConfiguration.set(String.valueOf(arenaRoot) + ".enabled", (Object)arena.isEnabled());
            fileConfiguration.set(String.valueOf(arenaRoot) + ".standaloneArenas", (Object)null);
            int i = 0;
            if (arena.getStandaloneArenas() != null) {
                Iterator<StandaloneArena> iterator = arena.getStandaloneArenas().iterator();
                while (iterator.hasNext()) {
                    StandaloneArena saArena = iterator.next();
                    String saA = CustomLocation.locationToString(saArena.getA());
                    String saB = CustomLocation.locationToString(saArena.getB());
                    String saMin = CustomLocation.locationToString(saArena.getMin());
                    String saMax = CustomLocation.locationToString(saArena.getMax());
                    String standAloneRoot = String.valueOf(arenaRoot) + ".standaloneArenas." + i;
                    fileConfiguration.set(String.valueOf(standAloneRoot) + ".a", (Object)saA);
                    fileConfiguration.set(String.valueOf(standAloneRoot) + ".b", (Object)saB);
                    fileConfiguration.set(String.valueOf(standAloneRoot) + ".min", (Object)saMin);
                    fileConfiguration.set(String.valueOf(standAloneRoot) + ".max", (Object)saMax);
                    ++i;
                }
            }
            return;
        });
        this.config.save();
    }

    public void reloadArenas() {
        this.saveArenas();
        this.arenas.clear();
        this.loadArenas();
    }

    public void openArenaSystemUI(Player player) {
        if (this.arenas.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No hay arenas.");
            return;
        }
        InventoryUI inventory = new InventoryUI("Sistema de Arenas", true, 6);
        for (Arena arena : this.arenas.values()) {
            ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.YELLOW + arena.getName() + ChatColor.GRAY + " (" + (arena.isEnabled() ? (String.valueOf(ChatColor.GREEN.toString()) + ChatColor.BOLD + "HABILITADA") : (String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD + "DESHABILITADA")) + ChatColor.GRAY + ")");
            ItemUtil.reloreItem(item, ChatColor.GRAY + "Arenas: " + ChatColor.GREEN + ((arena.getStandaloneArenas().isEmpty()) ? "Single Arena (Invisible Players)" : (String.valueOf(arena.getStandaloneArenas().size()) + " Arenas")), ChatColor.GRAY + "Arenas independientes: " + ChatColor.GREEN + ((arena.getAvailableArenas().isEmpty()) ? "Ninguna" : (String.valueOf(arena.getAvailableArenas().size()) + " Arenas disponibles")), "", String.valueOf(ChatColor.YELLOW.toString()) + ChatColor.BOLD + "CLICK IZQUIERDO " + ChatColor.GRAY + "Teletransporte a Arena", String.valueOf(ChatColor.YELLOW.toString()) + ChatColor.BOLD + "CLICK DERECHO " + ChatColor.GRAY + "Generar arenas independientes");
            inventory.addItem(new InventoryUI.AbstractClickableItem(item) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player)event.getWhoClicked();
                    if (event.getClick() == ClickType.LEFT) {
                        player.teleport(arena.getA().toBukkitLocation());
                    }
                    else {
                        InventoryUI generateInventory = new InventoryUI("Generar Arenas", true, 1);
                        int[] batches = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150 };
                        int[] array;
                        for (int length = (array = batches).length, i = 0; i < length; ++i) {
                            int batch = array[i];
                            ItemStack item = ItemUtil.createItem(Material.PAPER, String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD + batch + " ARENAS");
                            generateInventory.addItem(new InventoryUI.AbstractClickableItem(item) {
                                @Override
                                public void onClick(InventoryClickEvent event) {
                                    Player player = (Player)event.getWhoClicked();
                                    player.performCommand("arena generate " + arena.getName() + " " + batch);
                                    player.sendMessage(ChatColor.GREEN + "Generando " + batch + " arenas, compruebe la consola para ver el progreso.");
                                    player.closeInventory();
                                }
                            });
                        }
                        player.openInventory(generateInventory.getCurrentPage());
                    }
                }
            });
        }
        player.openInventory(inventory.getCurrentPage());
    }

    public void createArena(String name) {
        this.arenas.put(name, new Arena(name));
    }

    public void deleteArena(String name) {
        this.arenas.remove(name);
    }

    public Arena getArena(String name) {
        return this.arenas.get(name);
    }

    public Arena getRandomArena(Kit kit) {
        List<Arena> enabledArenas = new ArrayList<>();
        for (Arena arena : this.arenas.values()) {
            if (!arena.isEnabled()) {
                continue;
            }
            if (kit.getExcludedArenas().contains(arena.getName())) {
                continue;
            }
            if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
                continue;
            }
            enabledArenas.add(arena);
        }
        if (enabledArenas.isEmpty()) {
            return null;
        }
        return enabledArenas.get(ThreadLocalRandom.current().nextInt(enabledArenas.size()));
    }

    public void removeArenaMatchUUID(StandaloneArena arena) {
        this.arenaMatchUUIDs.remove(arena);
    }

    public UUID getArenaMatchUUID(StandaloneArena arena) {
        return this.arenaMatchUUIDs.get(arena);
    }

    public void setArenaMatchUUID(StandaloneArena arena, UUID matchUUID) {
        this.arenaMatchUUIDs.put(arena, matchUUID);
    }

    public Map<String, Arena> getArenas() {
        return this.arenas;
    }

    public Map<StandaloneArena, UUID> getArenaMatchUUIDs() {
        return this.arenaMatchUUIDs;
    }

    public int getGeneratingArenaRunnables() {
        return this.generatingArenaRunnables;
    }

    public void setGeneratingArenaRunnables(int generatingArenaRunnables) {
        this.generatingArenaRunnables = generatingArenaRunnables;
    }
}
