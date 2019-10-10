package net.latinplay.practice.managers;

import java.util.Collection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import net.latinplay.practice.file.Config;
import java.util.List;
import net.latinplay.practice.kit.Kit;
import java.util.Map;
import net.latinplay.practice.Practice;

public class KitManager
{
    private final Practice plugin;
    private final Map<String, Kit> kits;
    private final List<String> rankedKits;
    private final Config config;
    
    public KitManager() {
        this.plugin = Practice.getInstance();
        this.kits = new HashMap<>();
        this.rankedKits = new ArrayList<>();
        this.config = new Config("kits", this.plugin);
        this.loadKits();
        this.kits.entrySet().stream().filter(kit -> kit.getValue().isEnabled()).filter(kit -> kit.getValue().isRanked()).forEach(kit -> this.rankedKits.add(kit.getKey()));
    }
    
    private void loadKits() {
        final FileConfiguration fileConfig = this.config.getConfig();
        final ConfigurationSection kitSection = fileConfig.getConfigurationSection("kits");
        if (kitSection == null) {
            return;
        }
        final ConfigurationSection configurationSection = kitSection;
        kitSection.getKeys(false).forEach(name -> {
            ItemStack[] contents = (ItemStack[]) ((List)configurationSection.get(name + ".contents")).<ItemStack>toArray(new ItemStack[0]);
            ItemStack[] armor = (ItemStack[]) ((List)configurationSection.get(name + ".armor")).<ItemStack>toArray(new ItemStack[0]);
            ItemStack[] kitEditContents = (ItemStack[]) ((List)configurationSection.get(name + ".kitEditContents")).<ItemStack>toArray(new ItemStack[0]);
            List excludedArenas = configurationSection.getStringList(name + ".excludedArenas");
            List arenaWhiteList = configurationSection.getStringList(name + ".arenaWhitelist");
            ItemStack icon = (ItemStack)configurationSection.get(name + ".icon");
            boolean enabled = configurationSection.getBoolean(name + ".enabled");
            boolean ranked = configurationSection.getBoolean(name + ".ranked");
            boolean combo = configurationSection.getBoolean(name + ".combo");
            boolean sumo = configurationSection.getBoolean(name + ".sumo");
            boolean build = configurationSection.getBoolean(name + ".build");
            boolean spleef = configurationSection.getBoolean(name + ".spleef");
            boolean parkour = configurationSection.getBoolean(name + ".parkour");
            Kit kit = new Kit(name, contents, armor, kitEditContents, icon, excludedArenas, arenaWhiteList, enabled, ranked, combo, sumo, build, spleef, parkour);
            this.kits.put(name, kit);
        });
    }
    
    public void saveKits() {
        final FileConfiguration fileConfig = this.config.getConfig();
        fileConfig.set("kits", null);
        final FileConfiguration fileConfiguration = fileConfig;
        this.kits.forEach((kitName, kit) -> {
            if (kit.getIcon() != null && kit.getContents() != null && kit.getArmor() != null) {
                fileConfiguration.set("kits." + kitName + ".contents", kit.getContents());
                fileConfiguration.set("kits." + kitName + ".armor", kit.getArmor());
                fileConfiguration.set("kits." + kitName + ".kitEditContents", kit.getKitEditContents());
                fileConfiguration.set("kits." + kitName + ".icon", kit.getIcon());
                fileConfiguration.set("kits." + kitName + ".excludedArenas", kit.getExcludedArenas());
                fileConfiguration.set("kits." + kitName + ".arenaWhitelist", kit.getArenaWhiteList());
                fileConfiguration.set("kits." + kitName + ".enabled", kit.isEnabled());
                fileConfiguration.set("kits." + kitName + ".ranked", kit.isRanked());
                fileConfiguration.set("kits." + kitName + ".combo", kit.isCombo());
                fileConfiguration.set("kits." + kitName + ".sumo", kit.isSumo());
                fileConfiguration.set("kits." + kitName + ".build", kit.isBuild());
                fileConfiguration.set("kits." + kitName + ".spleef", kit.isSpleef());
                fileConfiguration.set("kits." + kitName + ".parkour", kit.isParkour());
            }
            return;
        });
        this.config.save();
    }
    
    public void deleteKit(final String name) {
        this.kits.remove(name);
    }
    
    public void createKit(final String name) {
        this.kits.put(name, new Kit(name));
    }
    
    public Collection<Kit> getKits() {
        return this.kits.values();
    }
    
    public Kit getKit(final String name) {
        return this.kits.get(name);
    }
    
    public List<String> getRankedKits() {
        return this.rankedKits;
    }
}
