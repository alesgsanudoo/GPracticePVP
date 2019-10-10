package me.groyteam.practice.listeners;

import org.bukkit.entity.Player;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.EntityEnderPearl;
import java.util.Iterator;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderPearl;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.HashMap;
import me.groyteam.practice.Practice;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import java.util.Map;
import org.bukkit.event.Listener;

public class EnderpearlListener implements Listener
{
    private final Map<EnderPearl, Location> validLocations;
    private final Practice plugin;
    
    public EnderpearlListener(final Practice plugin) {
        this.plugin = plugin;
        this.validLocations = new HashMap<>();
        this.runCheck();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }
    
    @EventHandler
    public void onLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            this.validLocations.put((EnderPearl)event.getEntity(), event.getEntity().getLocation());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            final EnderPearl pearl = this.lookupPearl(event.getPlayer(), event.getTo());
            if (pearl != null) {
                final Location validLocation = this.validLocations.get(pearl);
                if (validLocation != null) {
                    validLocation.setPitch(event.getPlayer().getLocation().getPitch());
                    validLocation.setYaw(event.getPlayer().getLocation().getYaw());
                    event.setTo(validLocation);
                }
            }
        }
    }
    
    private void runCheck() {
        new BukkitRunnable() {
            @Override
            public void run() {
                final Iterator<Map.Entry<EnderPearl, Location>> iterator = EnderpearlListener.this.validLocations.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Map.Entry<EnderPearl, Location> entry = iterator.next();
                    final EnderPearl pearlEntity = entry.getKey();
                    if (pearlEntity.isDead()) {
                        iterator.remove();
                    }
                    else {
                        final EntityEnderPearl entityEnderPearl = ((CraftEnderPearl)pearlEntity).getHandle();
                        final World worldServer = entityEnderPearl.world;
                        if (!worldServer.getCubes(entityEnderPearl, entityEnderPearl.getBoundingBox().grow(0.25, 0.25, 0.25)).isEmpty()) {
                            continue;
                        }
                        entry.setValue(pearlEntity.getLocation());
                    }
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, 1L, 1L);
    }
    
    private EnderPearl lookupPearl(final Player player, final Location to) {
        double distance = Double.MAX_VALUE;
        EnderPearl canidate = null;
        for (final EnderPearl enderpearl : this.validLocations.keySet()) {
            final double sqrt = to.distanceSquared(enderpearl.getLocation());
            if (enderpearl.getShooter() == player && sqrt < distance) {
                distance = sqrt;
                canidate = enderpearl;
            }
        }
        return canidate;
    }
}
