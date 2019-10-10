package me.groyteam.practice.runnable;

import com.boydti.fawe.util.EditSessionBuilder;
import com.boydti.fawe.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.groyteam.practice.match.Match;
import me.groyteam.practice.Practice;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchResetRunnable extends BukkitRunnable
{
    private final Practice plugin;
    private final Match match;
    
    @Override
    public void run() {
        if (this.match.getKit().isBuild()) {
            TaskManager.IMP.async(() -> {
                for(Location loc : this.match.getPlacedBlockLocations()) {
                    World w = Bukkit.getWorld("Arenas");
                    EditSession editSession = new EditSessionBuilder(w.getName()).fastmode(true).allowedRegionsEverywhere().autoQueue(false).limitUnlimited().build();
                    try {
                        editSession.setBlock(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getZ()), new BaseBlock(0));
                        editSession.setBlock(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), new BaseBlock(0));
                        editSession.setBlock(new Vector(loc.getX(), loc.getY(), loc.getZ()), new BaseBlock(0));
                    } catch (MaxChangedBlocksException ex) {
                        Logger.getLogger(MatchResetRunnable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    editSession.flushQueue();
                    TaskManager.IMP.task(() -> {
                        this.match.getPlacedBlockLocations().clear();
                        this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
                        this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
                        this.cancel();
                    });
                }
            });
	} else {
            this.cancel();
	}
    }
    
    public MatchResetRunnable(final Match match) {
        this.plugin = Practice.getInstance();
        this.match = match;
    }
}
