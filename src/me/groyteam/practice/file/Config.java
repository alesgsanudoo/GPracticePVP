package me.groyteam.practice.file;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.IOException;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public class Config
{
    private final FileConfiguration config;
    private final File configFile;
    protected boolean wasCreated;
    
    public Config(final String name, final JavaPlugin plugin) {
        this.configFile = new File(plugin.getDataFolder() + "/" + name + ".yml");
        if (!this.configFile.exists()) {
            try {
                this.configFile.getParentFile().mkdirs();
                this.configFile.createNewFile();
                
                this.wasCreated = true;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
    }
    
    public void save() {
        try {
            this.config.save(this.configFile);
            System.out.print("Archivo Guardado");
        }
        catch (IOException e) {
            System.out.print("Archivo no guardado");
        }
    }
    
    public FileConfiguration getConfig() {
        return this.config;
    }
    
    public File getConfigFile() {
        return this.configFile;
    }
    
    public boolean isWasCreated() {
        return this.wasCreated;
    }
}
