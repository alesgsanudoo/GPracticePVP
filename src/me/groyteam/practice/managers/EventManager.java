package me.groyteam.practice.managers;

import me.groyteam.practice.CustomLocation;
import me.groyteam.practice.Practice;
import me.groyteam.practice.events.EventState;
import me.groyteam.practice.events.PracticeEvent;
import me.groyteam.practice.events.oitc.OITCEvent;
import me.groyteam.practice.events.parkour.ParkourEvent;
import me.groyteam.practice.events.redrover.RedroverEvent;
import me.groyteam.practice.events.sumo.SumoEvent;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.player.PlayerState;

import java.util.List;
import org.bukkit.GameMode;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.WorldCreator;
import java.util.Arrays;

import org.bukkit.World;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class EventManager
{
    private final Map<Class<? extends PracticeEvent>, PracticeEvent> events;
    private final Practice plugin;
    private final HashMap<UUID, PracticeEvent> spectators;
    private long cooldown;
    private final World eventWorld;

    public EventManager() {
        this.events = new HashMap<>();
        this.plugin = Practice.getInstance();
        Arrays.<Class>asList(SumoEvent.class, OITCEvent.class, ParkourEvent.class, RedroverEvent.class).forEach(clazz -> this.addEvent(clazz));
        boolean newWorld;
        if (this.plugin.getServer().getWorld("event") == null) {
            this.eventWorld = this.plugin.getServer().createWorld(new WorldCreator("event"));
            newWorld = true;
        }
        else {
            this.eventWorld = this.plugin.getServer().getWorld("event");
            newWorld = false;
        }
        this.spectators = new HashMap<>();
        this.cooldown = 0L;
        if (this.eventWorld != null) {
            if (newWorld) {
                this.plugin.getServer().getWorlds().add(this.eventWorld);
            }
            this.eventWorld.setTime(2000L);
            this.eventWorld.setGameRuleValue("doDaylightCycle", "false");
            this.eventWorld.setGameRuleValue("doMobSpawning", "false");
            this.eventWorld.setStorm(false);
            this.eventWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        }
    }

    public PracticeEvent getByName(final String name) {
        return this.events.values().stream().filter(event -> event.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())).findFirst().orElse(null);
    }

    public void hostEvent(final PracticeEvent event, final Player host) {
        event.setState(EventState.WAITING);
        event.setHost(host);
        event.startCountdown();
    }

    private void addEvent(final Class<? extends PracticeEvent> clazz) {
        PracticeEvent event = null;
        try {
            event = (PracticeEvent)clazz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex2;
        }
        this.events.put(clazz, event);
    }

    public void addSpectatorRedrover(final Player player, final PlayerData playerData, final RedroverEvent event) {
        this.addSpectator(player, playerData, event);
        if (event.getSpawnLocations().size() == 1) {
            player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
        }
        else {
            final List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }
        for (final Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorSumo(final Player player, final PlayerData playerData, final SumoEvent event) {
        this.addSpectator(player, playerData, event);
        if (event.getSpawnLocations().size() == 1) {
            player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
        }
        else {
            final List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }
        for (final Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorOITC(final Player player, final PlayerData playerData, final OITCEvent event) {
        this.addSpectator(player, playerData, event);
        if (event.getSpawnLocations().size() == 1) {
            player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
        }
        else {
            final List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }
        for (final Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorParkour(final Player player, final PlayerData playerData, final ParkourEvent event) {
        this.addSpectator(player, playerData, event);
        if (event.getSpawnLocations().size() == 1) {
            player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
        }
        else {
            final List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }
        for (final Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    private void addSpectator(final Player player, final PlayerData playerData, final PracticeEvent event) {
        playerData.setPlayerState(PlayerState.SPECTATING);
        this.spectators.put(player.getUniqueId(), event);
        player.getInventory().setContents(this.plugin.getItemManager().getSpecItems());
        player.updateInventory();
        this.plugin.getServer().getOnlinePlayers().forEach(online -> {
            online.hidePlayer(player);
            player.hidePlayer(online);
        });
    }

    public void removeSpectator(final Player player) {
        this.getSpectators().remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }

    public boolean isPlaying(final Player player, final PracticeEvent event) {
        return event.getPlayers().containsKey(player.getUniqueId());
    }

    public PracticeEvent getEventPlaying(final Player player) {
        return this.events.values().stream().filter(event -> this.isPlaying(player, event)).findFirst().orElse(null);
    }

    public void setCooldown(final long cooldown) {
        this.cooldown = cooldown;
    }

    public Map<Class<? extends PracticeEvent>, PracticeEvent> getEvents() {
        return this.events;
    }

    public Practice getPlugin() {
        return this.plugin;
    }

    public HashMap<UUID, PracticeEvent> getSpectators() {
        return this.spectators;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public World getEventWorld() {
        return this.eventWorld;
    }
}
