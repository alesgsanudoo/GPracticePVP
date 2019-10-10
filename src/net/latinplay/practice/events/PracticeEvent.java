package net.latinplay.practice.events;

import java.util.function.Consumer;
import java.util.Map;
import org.bukkit.Bukkit;
import java.util.UUID;
import net.latinplay.practice.events.parkour.ParkourEvent;
import net.latinplay.practice.events.redrover.RedroverPlayer;
import net.latinplay.practice.events.redrover.RedroverEvent;
import net.latinplay.practice.events.sumo.SumoPlayer;
import net.latinplay.practice.events.sumo.SumoEvent;
import net.latinplay.practice.event.EventStartEvent;
import net.latinplay.practice.events.oitc.OITCPlayer;
import net.latinplay.practice.events.oitc.OITCEvent;
import java.util.Iterator;
import java.util.List;
import net.latinplay.practice.player.PlayerData;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import net.latinplay.practice.CustomLocation;
import net.latinplay.practice.util.PlayerUtil;
import net.latinplay.practice.player.PlayerState;
import java.util.stream.Collectors;
import java.util.Set;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import net.latinplay.practice.Practice;

public abstract class PracticeEvent<K extends EventPlayer>
{
    private final Practice plugin;
    private final String name;
    private int limit;
    private Player host;
    private EventState state;
    
    public void startCountdown() {
        if (this.getCountdownTask().isEnded()) {
            this.getCountdownTask().setTimeUntilStart(this.getCountdownTask().getCountdownTime());
            this.getCountdownTask().setEnded(false);
        }
        else {
            this.getCountdownTask().runTaskTimerAsynchronously(this.plugin, 20L, 20L);
        }
    }
    
    public void sendMessage(final String message) {
        this.getBukkitPlayers().forEach(player -> player.sendMessage(message));
    }
    
    public Set<Player> getBukkitPlayers() {
        return this.getPlayers().keySet().stream().filter(uuid -> this.plugin.getServer().getPlayer(uuid) != null).map(((Server)this.plugin.getServer())::getPlayer).collect(Collectors.toSet());
    }
    
    public void join(final Player player) {
        if (this.getPlayers().size() >= this.limit) {
            return;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.EVENT);
        PlayerUtil.clearPlayer(player);
        if (this.onJoin() != null) {
            this.onJoin().accept(player);
        }
        if (this.getSpawnLocations().size() == 1) {
            player.teleport(this.getSpawnLocations().get(0).toBukkitLocation());
        }
        else {
            final List<CustomLocation> spawnLocations = new ArrayList<>(this.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }
        this.plugin.getPlayerManager().giveLobbyItems(player);
        for (final Player other : this.getBukkitPlayers()) {
            other.showPlayer(player);
            player.showPlayer(other);
        }
        this.sendMessage("§a+ "+player.getName() + " §fha entrado al evento. §a(§a" + this.getPlayers().size() + " jugador" + ((this.getPlayers().size() == 1) ? "" : "es") + "§a)§f.");
    }
    
    public void leave(final Player player) {
        if (this instanceof OITCEvent) {
            final OITCEvent oitcEvent = (OITCEvent)this;
            final OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
            oitcPlayer.setState(OITCPlayer.OITCState.ELIMINATED);
        }
        if (this.onDeath() != null) {
            this.onDeath().accept(player);
        }
        this.getPlayers().remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }
    
    public void start() {
        new EventStartEvent(this).call();
        this.setState(EventState.STARTED);
        this.onStart();
        this.plugin.getEventManager().setCooldown(0L);
    }
    
    public void end() {
        this.plugin.getEventManager().setCooldown(System.currentTimeMillis() + 300000L);
        if (this instanceof SumoEvent) {
            final SumoEvent sumoEvent = (SumoEvent)this;
            
            for (SumoPlayer sumoPlayer : sumoEvent.getPlayers().values()) {
                if (sumoPlayer.getFightTask() != null) {
                    sumoPlayer.getFightTask().cancel();
                }
            }
            for(Player sPlayer : Bukkit.getOnlinePlayers()) {
                boolean inEvent = this.plugin.getEventManager().getEventPlaying(sPlayer) != null;
                if(inEvent) {
                    this.plugin.getPlayerManager().sendToSpawnEventFinish(sumoEvent, sPlayer.getPlayer());
                }
            }
            if (sumoEvent.getWaterCheckTask() != null) {
                sumoEvent.getWaterCheckTask().cancel();
            }
        }
        else if (this instanceof OITCEvent) {
            final OITCEvent oitcEvent = (OITCEvent)this;
            if (oitcEvent.getGameTask() != null) {
                oitcEvent.getGameTask().cancel();
            }
        }
        else if (this instanceof RedroverEvent) {
            final RedroverEvent redroverEvent = (RedroverEvent)this;
            for (final RedroverPlayer redroverPlayer : redroverEvent.getPlayers().values()) {
                if (redroverPlayer.getFightTask() != null) {
                    redroverPlayer.getFightTask().cancel();
                }
            }
            if (redroverEvent.getGameTask() != null) {
                redroverEvent.getGameTask().cancel();
            }
        }
        else if (this instanceof ParkourEvent) {
            final ParkourEvent parkourEvent = (ParkourEvent)this;
            if (parkourEvent.getGameTask() != null) {
                parkourEvent.getGameTask().cancel();
            }
            if (parkourEvent.getWaterCheckTask() != null) {
                parkourEvent.getWaterCheckTask().cancel();
            }
        }
        this.getPlayers().clear();
        this.setState(EventState.UNANNOUNCED);
        final Iterator<UUID> iterator = this.plugin.getEventManager().getSpectators().keySet().iterator();
        while (iterator.hasNext()) {
            final UUID spectatorUUID = iterator.next();
            final Player spectator = Bukkit.getPlayer(spectatorUUID);
            if (spectator != null) {
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getPlayerManager().sendToSpawnAndReset(spectator));
                iterator.remove();
            }
        }
        this.plugin.getEventManager().getSpectators().clear();
        this.getCountdownTask().setEnded(true);
    }
    
    public K getPlayer(final Player player) {
        return this.getPlayer(player.getUniqueId());
    }
    
    public K getPlayer(final UUID uuid) {
        return this.getPlayers().get(uuid);
    }
    
    public abstract Map<UUID, K> getPlayers();
    
    public abstract EventCountdownTask getCountdownTask();
    
    public abstract List<CustomLocation> getSpawnLocations();
    
    public abstract void onStart();
    
    public abstract Consumer<Player> onJoin();
    
    public abstract Consumer<Player> onDeath();
    
    public Practice getPlugin() {
        return this.plugin;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public Player getHost() {
        return this.host;
    }
    
    public EventState getState() {
        return this.state;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public void setHost(final Player host) {
        this.host = host;
    }
    
    public void setState(final EventState state) {
        this.state = state;
    }
    
    public PracticeEvent(final String name) {
        this.plugin = Practice.getInstance();
        this.state = EventState.UNANNOUNCED;
        this.name = name;
    }
}
