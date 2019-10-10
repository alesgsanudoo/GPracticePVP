package net.latinplay.practice;

import net.latinplay.practice.tablist.tablist;
import net.latinplay.practice.tablist.tablistAPI;
import org.bukkit.configuration.file.FileConfiguration;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.latinplay.practice.util.timer.impl.EnderpearlTimer;
import net.latinplay.practice.listeners.InventoryListener;
import net.latinplay.practice.settings.ProfileOptionsListeners;
import net.latinplay.practice.util.inventory.UIListener;
import net.latinplay.practice.listeners.EnderpearlListener;
import net.latinplay.practice.listeners.WorldListener;
import net.latinplay.practice.listeners.MatchListener;
import net.latinplay.practice.listeners.PlayerListener;
import net.latinplay.practice.listeners.EntityListener;

import java.util.Arrays;
import net.latinplay.practice.commands.management.TournamentCommand;
import net.latinplay.practice.commands.warp.WarpCommand;
import net.latinplay.practice.commands.management.SpawnsCommand;
import net.latinplay.practice.commands.InvCommand;
import net.latinplay.practice.commands.event.SpectateEventCommand;
import net.latinplay.practice.commands.StatsCommand;
import net.latinplay.practice.commands.management.KitCommand;
import net.latinplay.practice.commands.time.DayCommand;
import net.latinplay.practice.commands.duel.SpectateCommand;
import net.latinplay.practice.commands.duel.DuelCommand;
import net.latinplay.practice.commands.PartyCommand;
import net.latinplay.practice.commands.time.NightCommand;
import net.latinplay.practice.commands.management.ArenaCommand;
import net.latinplay.practice.commands.time.SunsetCommand;
import net.latinplay.practice.commands.duel.AcceptCommand;
import net.latinplay.practice.commands.event.EventManagerCommand;
import net.latinplay.practice.commands.event.HostCommand;
import net.latinplay.practice.commands.event.StatusEventCommand;
import net.latinplay.practice.commands.event.LeaveEventCommand;
import net.latinplay.practice.commands.event.JoinEventCommand;
import net.latinplay.practice.commands.management.ResetStatsCommand;
import net.latinplay.practice.commands.toggle.SettingsCommand;
import org.bukkit.command.Command;
import net.latinplay.practice.cache.StatusCache;
import net.latinplay.practice.runnable.ExpBarRunnable;
import net.latinplay.practice.runnable.SaveDataRunnable;
import net.latinplay.practice.board.PracticeBoard;
import com.bizarrealex.aether.Aether;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import net.latinplay.practice.handler.CustomMovementHandler;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.clip.placeholderapi.PlaceholderAPI;
import net.latinplay.practice.cache.MenuListener;
import net.latinplay.practice.player.PlayerData;
import net.latinplay.practice.util.timer.TimerManager;
import net.latinplay.practice.managers.ChunkManager;
import net.latinplay.practice.managers.TournamentManager;
import net.latinplay.practice.managers.SpawnManager;
import net.latinplay.practice.ffa.FFAManager;
import net.latinplay.practice.managers.KitManager;
import net.latinplay.practice.managers.ItemManager;
import net.latinplay.practice.managers.EventManager;
import net.latinplay.practice.managers.QueueManager;
import net.latinplay.practice.managers.PartyManager;
import net.latinplay.practice.managers.MatchManager;
import net.latinplay.practice.managers.ArenaManager;
import net.latinplay.practice.managers.PlayerManager;
import net.latinplay.practice.managers.EditorManager;
import net.latinplay.practice.managers.InventoryManager;
import net.latinplay.practice.file.Config;
import net.latinplay.practice.leaderheads.EloLeader;
import org.bukkit.scheduler.BukkitRunnable;
import net.latinplay.practice.leaderheads.WinsLeader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import spg.lgdev.iSpigot;

public class Practice extends JavaPlugin
{
    private static Practice instance;
    private Config mainConfig;
    private InventoryManager inventoryManager;
    private EditorManager editorManager;
    private PlayerManager playerManager;
    private ArenaManager arenaManager;
    private MatchManager matchManager;
    private PartyManager partyManager;
    private QueueManager queueManager;
    private EventManager eventManager;
    private ItemManager itemManager;
    private KitManager kitManager;
    private FFAManager ffaManager;
    private SpawnManager spawnManager;
    private TournamentManager tournamentManager;
    private ChunkManager chunkManager;
    private TimerManager timerManager;

    @Override
    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerData playerData = this.getPlayerManager().getPlayerData(player.getUniqueId());
            this.getPlayerManager().saveData(playerData);
        }
        for (final PlayerData playerData : this.playerManager.getAllData()) {
            this.playerManager.saveData(playerData);
        }
        this.arenaManager.saveArenas();
        this.kitManager.saveKits();
        this.spawnManager.saveSpawnConfig();
        if(this.spawnManager.getOitcLocation() != null){
            this.spawnManager.saveOitcConfig();
        }
        if(this.spawnManager.getParkourLocation() != null) {
            this.spawnManager.saveParkourConfig();
        }
        if(this.spawnManager.getSumoLocation() != null) {
            this.spawnManager.saveSumoConfig();
        }
        if(this.spawnManager.getEditorLocation() != null) {
            this.spawnManager.saveEditConfig();
        }
        if(this.spawnManager.getRedroverLocation() != null) {
            this.spawnManager.saveRedroverConfig();
        }
        World w = Bukkit.getWorld("Arenas");
        this.reset(w);
    }

    @Override
    public void onEnable() {
        Practice.instance = this;
        this.mainConfig = new Config("config", this);
        iSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler());
        this.registerCommands();
        this.registerListeners();
        this.registerManagers();
        this.TabUpdater();
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveDataRunnable(), 6000L, 6000L);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ExpBarRunnable(), 2L, 2L);
        new StatusCache().start();
        this.saveDefaultConfig();
        this.saveConfig();
        new WinsLeader(this);
        new EloLeader(this);
        new Aether(this, new PracticeBoard());
    }

    private void registerListeners() {
        Arrays.asList(new EntityListener(), new PlayerListener(), new MatchListener(), new tablist( this), new WorldListener(), new EnderpearlListener(this), new UIListener(), new ProfileOptionsListeners(), new MenuListener(), new InventoryListener()).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }

    private void registerCommands() {
        Arrays.<Command>asList(new SettingsCommand(),new ResetStatsCommand(), new JoinEventCommand(), new LeaveEventCommand(), new StatusEventCommand(), new HostCommand(), new EventManagerCommand(), new AcceptCommand(), new SunsetCommand(), new ArenaCommand(), new NightCommand(), new PartyCommand(), new DuelCommand(), new SpectateCommand(), new DayCommand(), new KitCommand(), new StatsCommand(), new SpectateEventCommand(), new InvCommand(), new SpawnsCommand(), new WarpCommand(), new TournamentCommand()).forEach(command -> this.registerCommand(command, this.getName()));
    }

    private void registerManagers() {
        this.spawnManager = new SpawnManager();
        this.arenaManager = new ArenaManager();
        this.chunkManager = new ChunkManager();
        this.editorManager = new EditorManager();
        this.itemManager = new ItemManager();
        this.kitManager = new KitManager();
        this.matchManager = new MatchManager();
        this.partyManager = new PartyManager();
        this.playerManager = new PlayerManager();
        this.queueManager = new QueueManager();
        this.inventoryManager = new InventoryManager();
        this.eventManager = new EventManager();
        this.tournamentManager = new TournamentManager();
        this.timerManager = new TimerManager(this);
        if (this.timerManager.getTimer(EnderpearlTimer.class) == null) {
            this.timerManager.registerTimer(new EnderpearlTimer());
        }
    }

    public void registerCommand(final Command cmd, final String fallbackPrefix) {
        MinecraftServer.getServer().server.getCommandMap().register(cmd.getName(), fallbackPrefix, cmd);
    }


    public void reset(World world) {
        Bukkit.getServer().unloadWorld(world, false);
        File srcWorldFolder = new File("plugins/GPractice/" + world.getName() + "/" + world.getName());
        File worldFolder = new File(world.getName());
        deleteFolder(worldFolder);
        copyWorldFolder(srcWorldFolder, worldFolder); // Copy backup folder
        //WorldCreator w = new WorldCreator(world.getName()); // This starts the world load
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files != null) {
            for(File file : files) {
                if(file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    public void updateTab(final Player p) {
        final FileConfiguration c = this.getConfig();
        final String headline = setPlaceholders(p, c.getString("Tablist.header").replace("&", "§").replace("%player%", p.getName()));
        final String footer = setPlaceholders(p, c.getString("Tablist.footer").replace("&", "§").replace("%player%", p.getName()));
        tablistAPI.tablistAPI(p, headline, footer);
    }

    public void TabUpdater() {
        if (getInstance().getConfig().getBoolean("Tablist.Enable")) {
            new BukkitRunnable() {
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(pl -> Practice.this.updateTab(pl));
                }
            }.runTaskTimerAsynchronously(get(), (long)(this.getConfig().getInt("Online.Time-Update") * 20), (long)(this.getConfig().getInt("Online.Time-Update") * 20));
        }
    }

    public static Practice get() {
        return getInstance();
    }

    private void copyWorldFolder(File from, File to) {
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.dat"));
            if(!ignore.contains(from.getName())) {
                if(from.isDirectory()) {
                    if(!to.exists()) {
                        to.mkdirs();
                    }
                    String[] files = from.list();
                    for(String file : files) {
                        File srcFile = new File(from, file);
                        File destFile = new File(to, file);
                        copyWorldFolder(srcFile, destFile);
                    }
                } else {
                    OutputStream out;
                    try (InputStream in = new FileInputStream(from)) {
                        out = new FileOutputStream(to);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = in.read(buffer)) > 0) {
                            out.write(buffer, 0, length);
                        }
                    }
                    out.close();
                }
            }
        } catch(FileNotFoundException e) {
            Logger.getLogger(Practice.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException ex) {
            Logger.getLogger(Practice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Practice getInstance() {
        return Practice.instance;
    }

    public Config getMainConfig() {
        return this.mainConfig;
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public EditorManager getEditorManager() {
        return this.editorManager;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public ArenaManager getArenaManager() {
        return this.arenaManager;
    }

    public MatchManager getMatchManager() {
        return this.matchManager;
    }

    public PartyManager getPartyManager() {
        return this.partyManager;
    }

    public QueueManager getQueueManager() {
        return this.queueManager;
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public ItemManager getItemManager() {
        return this.itemManager;
    }

    public KitManager getKitManager() {
        return this.kitManager;
    }

    public FFAManager getFfaManager() {
        return this.ffaManager;
    }

    public SpawnManager getSpawnManager() {
        return this.spawnManager;
    }

    public TournamentManager getTournamentManager() {
        return this.tournamentManager;
    }

    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }

    public static String setPlaceholders(Player p, String s) {
        s = PlaceholderAPI.setPlaceholders(p, s);
        return s;
    }

    public TimerManager getTimerManager() {
        return this.timerManager;
    }
}
