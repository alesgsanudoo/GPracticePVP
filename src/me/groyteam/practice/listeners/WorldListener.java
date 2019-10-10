package me.groyteam.practice.listeners;

import me.groyteam.practice.Practice;
import me.groyteam.practice.arena.StandaloneArena;
import me.groyteam.practice.match.Match;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.player.PlayerState;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;
import org.bukkit.block.Block;

public class WorldListener implements Listener
{
    private Practice plugin;

    public WorldListener() {
        this.plugin = Practice.getInstance();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().log(Level.WARNING, "{0}''s player data is null", String.valueOf(player.getName()));
            event.setCancelled(true);
            return;
        }
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match.getKit().isBuild()) {
                if (!match.getPlacedBlockLocations().contains(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
        else if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().log(Level.WARNING, "{0}''s player data is null", String.valueOf(player.getName()));
            event.setCancelled(true);
            return;
        }
        if (playerData.getPlayerState() != PlayerState.FIGHTING) {
            if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
            return;
        }
        Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
        if (match == null) {
            event.setCancelled(true);
            return;
        }
        if (!match.getKit().isBuild()) {
            event.setCancelled(true);
        }
        else {
            if(match.getStandaloneArena() == null) {
                return;
            }
            double minX = match.getStandaloneArena().getMin().getX();
            double minZ = match.getStandaloneArena().getMin().getZ();
            double maxX = match.getStandaloneArena().getMax().getX();
            double maxZ = match.getStandaloneArena().getMax().getZ();
            if (minX > maxX) {
                double lastMinX = minX;
                minX = maxX;
                maxX = lastMinX;
            }
            if (minZ > maxZ) {
                double lastMinZ = minZ;
                minZ = maxZ;
                maxZ = lastMinZ;
            }
            if (player.getLocation().getX() >= minX && player.getLocation().getX() <= maxX && player.getLocation().getZ() >= minZ && player.getLocation().getZ() <= maxZ) {
                if (player.getLocation().getY() - match.getStandaloneArena().getA().getY() < 5.0 && event.getBlockPlaced() != null) {
                    match.addPlacedBlockLocation(event.getBlockPlaced().getLocation());
                }
                else {
                    event.setCancelled(true);
                }
            }
            else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().log(Level.WARNING, "{0}''s player data is null", String.valueOf(player.getName()));
            event.setCancelled(true);
            return;
        }
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (!match.getKit().isBuild()) {
                event.setCancelled(true);
            }
            else {
                if(match.getStandaloneArena() == null) {
                    return;
                }
                double minX = match.getStandaloneArena().getMin().getX();
                double minZ = match.getStandaloneArena().getMin().getZ();
                double maxX = match.getStandaloneArena().getMax().getX();
                double maxZ = match.getStandaloneArena().getMax().getZ();
                if (minX > maxX) {
                    double lastMinX = minX;
                    minX = maxX;
                    maxX = lastMinX;
                }
                if (minZ > maxZ) {
                    double lastMinZ = minZ;
                    minZ = maxZ;
                    maxZ = lastMinZ;
                }
                if (player.getLocation().getX() >= minX && player.getLocation().getX() <= maxX && player.getLocation().getZ() >= minZ && player.getLocation().getZ() <= maxZ) {
                    if (player.getLocation().getY() - match.getStandaloneArena().getA().getY() < 5.0) {
                        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
                        match.addPlacedBlockLocation(block.getLocation());
                    }
                    else {
                        event.setCancelled(true);
                    }
                }
                else {
                    event.setCancelled(true);
                }
            }
            return;
        }
        if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.getToBlock() == null) {
            return;
        }
        for (StandaloneArena arena : this.plugin.getArenaManager().getArenaMatchUUIDs().keySet()) {
            if(arena == null) {
                return;
            }
            double minX = arena.getMin().getX();
            double minZ = arena.getMin().getZ();
            double maxX = arena.getMax().getX();
            double maxZ = arena.getMax().getZ();
            if (minX > maxX) {
                double lastMinX = minX;
                minX = maxX;
                maxX = lastMinX;
            }
            if (minZ > maxZ) {
                double lastMinZ = minZ;
                minZ = maxZ;
                maxZ = lastMinZ;
            }
            if (event.getToBlock().getX() >= minX && event.getToBlock().getZ() >= minZ && event.getToBlock().getX() <= maxX && event.getToBlock().getZ() <= maxZ) {
                UUID matchUUID = this.plugin.getArenaManager().getArenaMatchUUID(arena);
                Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);

                match.addPlacedBlockLocation(event.getToBlock().getLocation());
                break;
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }
}
