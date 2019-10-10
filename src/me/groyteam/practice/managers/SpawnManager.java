package me.groyteam.practice.managers;

import me.groyteam.practice.CustomLocation;
import me.groyteam.practice.Practice;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.ArrayList;
import java.util.List;

public class SpawnManager
{
    private final Practice plugin;
    private CustomLocation spawnLocation;
    private CustomLocation spawnMin;
    private CustomLocation spawnMax;
    private CustomLocation editorLocation;
    private CustomLocation editorMin;
    private CustomLocation editorMax;
    private CustomLocation sumoLocation;
    private CustomLocation sumoFirst;
    private CustomLocation sumoSecond;
    private CustomLocation sumoMin;
    private CustomLocation sumoMax;
    private CustomLocation oitcLocation;
    private List<CustomLocation> oitcSpawnpoints;
    private CustomLocation oitcMin;
    private CustomLocation oitcMax;
    private CustomLocation parkourLocation;
    private CustomLocation parkourGameLocation;
    private CustomLocation parkourMin;
    private CustomLocation parkourMax;
    private CustomLocation redroverLocation;
    private CustomLocation redroverFirst;
    private CustomLocation redroverSecond;
    private CustomLocation redroverMin;
    private CustomLocation redroverMax;
    
    public SpawnManager() {
        this.plugin = Practice.getInstance();
        this.oitcSpawnpoints = new ArrayList<>();
        this.loadConfig();
    }
    
    private void loadConfig() {
        final FileConfiguration config = this.plugin.getMainConfig().getConfig();
        if (config.contains("spawnLocation")) {
            this.spawnLocation = CustomLocation.stringToLocation(config.getString("spawnLocation"));
            this.spawnMin = CustomLocation.stringToLocation(config.getString("spawnMin"));
            this.spawnMax = CustomLocation.stringToLocation(config.getString("spawnMax"));
        }
        if (config.contains("editorLocation")) {
            this.editorLocation = CustomLocation.stringToLocation(config.getString("editorLocation"));
            this.editorMin = CustomLocation.stringToLocation(config.getString("editorMin"));
            this.editorMax = CustomLocation.stringToLocation(config.getString("editorMax"));
        }
        if (config.contains("sumoLocation")) {
            this.sumoLocation = CustomLocation.stringToLocation(config.getString("sumoLocation"));
            this.sumoMin = CustomLocation.stringToLocation(config.getString("sumoMin"));
            this.sumoMax = CustomLocation.stringToLocation(config.getString("sumoMax"));
            this.sumoFirst = CustomLocation.stringToLocation(config.getString("sumoFirst"));
            this.sumoSecond = CustomLocation.stringToLocation(config.getString("sumoSecond"));
        }
        if (config.contains("oitcLocation")) {
            this.oitcLocation = CustomLocation.stringToLocation(config.getString("oitcLocation"));
            this.oitcMin = CustomLocation.stringToLocation(config.getString("oitcMin"));
            this.oitcMax = CustomLocation.stringToLocation(config.getString("oitcMax"));
            for (final String spawnpoint : config.getStringList("oitcSpawnpoints")) {
                this.oitcSpawnpoints.add(CustomLocation.stringToLocation(spawnpoint));
            }
        }
        if (config.contains("redroverLocation")) {
            this.redroverLocation = CustomLocation.stringToLocation(config.getString("redroverLocation"));
            this.redroverMin = CustomLocation.stringToLocation(config.getString("redroverMin"));
            this.redroverMax = CustomLocation.stringToLocation(config.getString("redroverMax"));
            this.redroverFirst = CustomLocation.stringToLocation(config.getString("redroverFirst"));
            this.redroverSecond = CustomLocation.stringToLocation(config.getString("redroverSecond"));
        }
        if (config.contains("parkourLocation")) {
            this.parkourLocation = CustomLocation.stringToLocation(config.getString("parkourLocation"));
            this.parkourGameLocation = CustomLocation.stringToLocation(config.getString("parkourGameLocation"));
            this.parkourMin = CustomLocation.stringToLocation(config.getString("parkourMin"));
            this.parkourMax = CustomLocation.stringToLocation(config.getString("parkourMax"));
        }
    }
    
    public void saveSpawnConfig() {
        FileConfiguration config = this.plugin.getMainConfig().getConfig();
        config.set("spawnLocation", CustomLocation.locationToString(this.spawnLocation));
        config.set("spawnMin", CustomLocation.locationToString(this.spawnMin));
        config.set("spawnMax", CustomLocation.locationToString(this.spawnMax));
        this.plugin.getMainConfig().save();
    }
    
    public void saveRedroverConfig() {
        FileConfiguration config = this.plugin.getMainConfig().getConfig();
        config.set("redroverLocation", CustomLocation.locationToString(this.redroverLocation));
        config.set("redroverMin", CustomLocation.locationToString(this.redroverMin));
        config.set("redroverMax", CustomLocation.locationToString(this.redroverMax));
        config.set("redroverFirst", CustomLocation.locationToString(this.redroverFirst));
        config.set("redroverSecond", CustomLocation.locationToString(this.redroverSecond));
        this.plugin.getMainConfig().save();
    }
    
    public void saveEditConfig() {
        FileConfiguration config = this.plugin.getMainConfig().getConfig();
        config.set("editorLocation", CustomLocation.locationToString(this.editorLocation));
        config.set("editorMin", CustomLocation.locationToString(this.editorMin));
        config.set("editorMax", CustomLocation.locationToString(this.editorMax));
        this.plugin.getMainConfig().save();
    }
    
    public void saveSumoConfig() {
        FileConfiguration config = this.plugin.getMainConfig().getConfig();
        config.set("sumoLocation", CustomLocation.locationToString(this.sumoLocation));
        config.set("sumoMin", CustomLocation.locationToString(this.sumoMin));
        config.set("sumoMax", CustomLocation.locationToString(this.sumoMax));
        config.set("sumoFirst", CustomLocation.locationToString(this.sumoFirst));
        config.set("sumoSecond", CustomLocation.locationToString(this.sumoSecond));
        this.plugin.getMainConfig().save();
    }
    
    public void saveOitcConfig() {
        FileConfiguration config = this.plugin.getMainConfig().getConfig();
        config.set("oitcLocation", CustomLocation.locationToString(this.oitcLocation));
        config.set("oitcMin", CustomLocation.locationToString(this.oitcMin));
        config.set("oitcMax", CustomLocation.locationToString(this.oitcMax));
        config.set("oitcSpawnpoints", this.fromLocations(this.oitcSpawnpoints));
        this.plugin.getMainConfig().save();
    }
    
    public void saveParkourConfig() {
        FileConfiguration config = this.plugin.getMainConfig().getConfig();
        config.set("parkourLocation", CustomLocation.locationToString(this.parkourLocation));
        config.set("parkourGameLocation", CustomLocation.locationToString(this.parkourGameLocation));
        config.set("parkourMin", CustomLocation.locationToString(this.parkourMin));
        config.set("parkourMax", CustomLocation.locationToString(this.parkourMax));
        this.plugin.getMainConfig().save();
    }
    
    private List<String> fromLocations(final List<CustomLocation> locations) {
        final List<String> toReturn = new ArrayList<>();
        for (final CustomLocation location : locations) {
            toReturn.add(CustomLocation.locationToString(location));
        }
        return toReturn;
    }
    
    public Practice getPlugin() {
        return this.plugin;
    }
    
    public CustomLocation getSpawnLocation() {
        return this.spawnLocation;
    }
    
    public CustomLocation getSpawnMin() {
        return this.spawnMin;
    }
    
    public CustomLocation getSpawnMax() {
        return this.spawnMax;
    }
    
    public CustomLocation getEditorLocation() {
        return this.editorLocation;
    }
    
    public CustomLocation getEditorMin() {
        return this.editorMin;
    }
    
    public CustomLocation getEditorMax() {
        return this.editorMax;
    }
    
    public CustomLocation getSumoLocation() {
        return this.sumoLocation;
    }
    
    public CustomLocation getSumoFirst() {
        return this.sumoFirst;
    }
    
    public CustomLocation getSumoSecond() {
        return this.sumoSecond;
    }
    
    public CustomLocation getSumoMin() {
        return this.sumoMin;
    }
    
    public CustomLocation getSumoMax() {
        return this.sumoMax;
    }
    
    public CustomLocation getOitcLocation() {
        return this.oitcLocation;
    }
    
    public List<CustomLocation> getOitcSpawnpoints() {
        return this.oitcSpawnpoints;
    }
    
    public CustomLocation getOitcMin() {
        return this.oitcMin;
    }
    
    public CustomLocation getOitcMax() {
        return this.oitcMax;
    }
    
    public CustomLocation getParkourLocation() {
        return this.parkourLocation;
    }
    
    public CustomLocation getParkourGameLocation() {
        return this.parkourGameLocation;
    }
    
    public CustomLocation getParkourMin() {
        return this.parkourMin;
    }
    
    public CustomLocation getParkourMax() {
        return this.parkourMax;
    }
    
    public CustomLocation getRedroverLocation() {
        return this.redroverLocation;
    }
    
    public CustomLocation getRedroverFirst() {
        return this.redroverFirst;
    }
    
    public CustomLocation getRedroverSecond() {
        return this.redroverSecond;
    }
    
    public CustomLocation getRedroverMin() {
        return this.redroverMin;
    }
    
    public CustomLocation getRedroverMax() {
        return this.redroverMax;
    }
    
    public void setSpawnLocation(final CustomLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
    
    public void setSpawnMin(final CustomLocation spawnMin) {
        this.spawnMin = spawnMin;
    }
    
    public void setSpawnMax(final CustomLocation spawnMax) {
        this.spawnMax = spawnMax;
    }
    
    public void setEditorLocation(final CustomLocation editorLocation) {
        this.editorLocation = editorLocation;
    }
    
    public void setEditorMin(final CustomLocation editorMin) {
        this.editorMin = editorMin;
    }
    
    public void setEditorMax(final CustomLocation editorMax) {
        this.editorMax = editorMax;
    }
    
    public void setSumoLocation(final CustomLocation sumoLocation) {
        this.sumoLocation = sumoLocation;
    }
    
    public void setSumoFirst(final CustomLocation sumoFirst) {
        this.sumoFirst = sumoFirst;
    }
    
    public void setSumoSecond(final CustomLocation sumoSecond) {
        this.sumoSecond = sumoSecond;
    }
    
    public void setSumoMin(final CustomLocation sumoMin) {
        this.sumoMin = sumoMin;
    }
    
    public void setSumoMax(final CustomLocation sumoMax) {
        this.sumoMax = sumoMax;
    }
    
    public void setOitcLocation(final CustomLocation oitcLocation) {
        this.oitcLocation = oitcLocation;
    }
    
    public void setOitcSpawnpoints(final List<CustomLocation> oitcSpawnpoints) {
        this.oitcSpawnpoints = oitcSpawnpoints;
    }
    
    public void setOitcMin(final CustomLocation oitcMin) {
        this.oitcMin = oitcMin;
    }
    
    public void setOitcMax(final CustomLocation oitcMax) {
        this.oitcMax = oitcMax;
    }
    
    public void setParkourLocation(final CustomLocation parkourLocation) {
        this.parkourLocation = parkourLocation;
    }
    
    public void setParkourGameLocation(final CustomLocation parkourGameLocation) {
        this.parkourGameLocation = parkourGameLocation;
    }
    
    public void setParkourMin(final CustomLocation parkourMin) {
        this.parkourMin = parkourMin;
    }
    
    public void setParkourMax(final CustomLocation parkourMax) {
        this.parkourMax = parkourMax;
    }
    
    public void setRedroverLocation(final CustomLocation redroverLocation) {
        this.redroverLocation = redroverLocation;
    }
    
    public void setRedroverFirst(final CustomLocation redroverFirst) {
        this.redroverFirst = redroverFirst;
    }
    
    public void setRedroverSecond(final CustomLocation redroverSecond) {
        this.redroverSecond = redroverSecond;
    }
    
    public void setRedroverMin(final CustomLocation redroverMin) {
        this.redroverMin = redroverMin;
    }
    
    public void setRedroverMax(final CustomLocation redroverMax) {
        this.redroverMax = redroverMax;
    }
}
