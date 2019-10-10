package net.latinplay.practice.handler;

import net.latinplay.practice.events.PracticeEvent;
import net.latinplay.practice.player.PlayerData;
import net.latinplay.practice.events.sumo.SumoPlayer;
import net.latinplay.practice.events.sumo.SumoEvent;
import net.latinplay.practice.match.MatchState;
import net.latinplay.practice.util.BlockUtil;
import net.latinplay.practice.player.PlayerState;
import net.latinplay.practice.CustomLocation;
import net.latinplay.practice.match.Match;
import net.latinplay.practice.Practice;
import spg.lgdev.handler.MovementHandler;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.HashMap;
import java.util.logging.Level;

public class CustomMovementHandler implements MovementHandler
{
    public static Practice plugin;
    private static final HashMap<Match, HashMap<UUID, CustomLocation>> parkourCheckpoints = new HashMap<>();;

    public CustomMovementHandler() {
        CustomMovementHandler.plugin = Practice.getInstance();
    }

    public static HashMap<Match, HashMap<UUID, CustomLocation>> getParkourCheckpoints() {
        return CustomMovementHandler.parkourCheckpoints;
    }

    @Override
    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
        PlayerData playerData = CustomMovementHandler.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            CustomMovementHandler.plugin.getLogger().log(Level.WARNING, "{0}''s player data is null", String.valueOf(player.getName()));
            return;
        }
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = CustomMovementHandler.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match == null) {
                return;
            }
            if (match.getKit().isSumo()) {
                if (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1)) {
                    CustomMovementHandler.plugin.getMatchManager().removeFighter(player, playerData, true);
                }
                if ((to.getX() != from.getX() || to.getZ() != from.getZ()) && match.getMatchState() == MatchState.STARTING) {
                    player.teleport(from);
                    player.teleport(from);
                    ((CraftPlayer)player).getHandle().playerConnection.checkMovement = false;
                }
            }
        }
        PracticeEvent event = CustomMovementHandler.plugin.getEventManager().getEventPlaying(player);
        if (event != null) {
            if (event instanceof SumoEvent) {
                SumoEvent sumoEvent = (SumoEvent)event;
                if (sumoEvent.getPlayer(player).getFighting() != null && sumoEvent.getPlayer(player).getState() == SumoPlayer.SumoState.PREPARING) {
                    player.teleport(from);
                    player.teleport(from);
                    ((CraftPlayer)player).getHandle().playerConnection.checkMovement = false;
                }
            }
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {
    }
}
