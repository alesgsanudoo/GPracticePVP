package net.latinplay.practice.match;

import java.util.Objects;
import java.util.stream.Stream;
import net.latinplay.practice.util.Clickable;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.util.Arrays;
import io.netty.util.internal.ConcurrentSet;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.HashMap;
import net.latinplay.practice.arena.StandaloneArena;
import net.latinplay.practice.kit.Kit;
import net.latinplay.practice.arena.Arena;
import net.latinplay.practice.queue.QueueType;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import java.util.Set;
import net.latinplay.practice.inventory.InventorySnapshot;
import java.util.UUID;
import java.util.Map;
import net.latinplay.practice.Practice;

public class Match
{
    private final Practice plugin;
    private final Map<UUID, InventorySnapshot> snapshots;
    private final Set<Entity> entitiesToRemove;
    private final Set<BlockState> originalBlockChanges;
    private final Set<Location> placedBlockLocations;
    private final Set<UUID> spectators;
    private final Set<Integer> runnables;
    private final Set<UUID> haveSpectated;
    private final List<MatchTeam> teams;
    private final UUID matchId;
    private final QueueType type;
    private final Arena arena;
    private final Kit kit;
    private final boolean redrover;
    private StandaloneArena standaloneArena;
    private MatchState matchState;
    private int winningTeamId;
    private int countdown;
    
    public Match(final Arena arena, final Kit kit, final QueueType type, final MatchTeam... teams) {
        this(arena, kit, type, false, teams);
    }
    
    public Match(final Arena arena, final Kit kit, final QueueType type, final boolean redrover, final MatchTeam... teams) {
        this.plugin = Practice.getInstance();
        this.snapshots = new HashMap<>();
        this.entitiesToRemove = new HashSet<>();
        this.originalBlockChanges = Sets.newConcurrentHashSet();
        this.placedBlockLocations = Sets.newConcurrentHashSet();
        this.spectators = (Set<UUID>)new ConcurrentSet();
        this.runnables = new HashSet<>();
        this.haveSpectated = new HashSet<>();
        this.matchId = UUID.randomUUID();
        this.matchState = MatchState.STARTING;
        this.countdown = 6;
        this.arena = arena;
        this.kit = kit;
        this.type = type;
        this.redrover = redrover;
        this.teams = Arrays.<MatchTeam>asList(teams);
    }
    
    public void addSpectator(final UUID uuid) {
        this.spectators.add(uuid);
    }
    
    public void removeSpectator(final UUID uuid) {
        this.spectators.remove(uuid);
    }
    
    public void addHaveSpectated(final UUID uuid) {
        this.haveSpectated.add(uuid);
    }
    
    public boolean haveSpectated(final UUID uuid) {
        return this.haveSpectated.contains(uuid);
    }
    
    public void addSnapshot(final Player player) {
        this.snapshots.put(player.getUniqueId(), new InventorySnapshot(player, this));
    }
    
    public boolean hasSnapshot(final UUID uuid) {
        return this.snapshots.containsKey(uuid);
    }
    
    public InventorySnapshot getSnapshot(final UUID uuid) {
        return this.snapshots.get(uuid);
    }
    
    public void addEntityToRemove(final Entity entity) {
        this.entitiesToRemove.add(entity);
    }
    
    public void removeEntityToRemove(final Entity entity) {
        this.entitiesToRemove.remove(entity);
    }
    
    public void clearEntitiesToRemove() {
        this.entitiesToRemove.clear();
    }
    
    public void addRunnable(final int id) {
        this.runnables.add(id);
    }
    
    public void broadcastWithSound(final String message, final Sound sound) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> {
            player.sendMessage(message);
            player.playSound(player.getLocation(), sound, 10.0f, 1.0f);
        }));
        this.spectatorPlayers().forEach(spectator -> {
            spectator.sendMessage(message);
            spectator.playSound(spectator.getLocation(), sound, 10.0f, 1.0f);
        });
    }
    
    public void broadcast(final String message) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> player.sendMessage(message)));
        this.spectatorPlayers().forEach(spectator -> spectator.sendMessage(message));
    }
    
    public void broadcast(final Clickable message) {
        this.teams.forEach(team -> team.alivePlayers().forEach(message::sendToPlayer));
        this.spectatorPlayers().forEach(message::sendToPlayer);
    }
    
    public Stream<Player> spectatorPlayers() {
        return this.spectators.stream().<Player>map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }
    
    public int decrementCountdown() {
        return --this.countdown;
    }
    
    public boolean isParty() {
        return this.isFFA() || (this.teams.get(0).getPlayers().size() != 1 && this.teams.get(1).getPlayers().size() != 1);
    }
    
    public boolean isPartyMatch() {
        return this.isFFA() || this.teams.get(0).getPlayers().size() >= 2 || this.teams.get(1).getPlayers().size() >= 2;
    }
    
    public boolean isFFA() {
        return this.teams.size() == 1;
    }
    
    public Map<UUID, InventorySnapshot> getSnapshots() {
        return this.snapshots;
    }
    
    public Set<Entity> getEntitiesToRemove() {
        return this.entitiesToRemove;
    }
    
    public Set<BlockState> getOriginalBlockChanges() {
        return this.originalBlockChanges;
    }
    
    public Set<Location> getPlacedBlockLocations() {
        return this.placedBlockLocations;
    }
    
    public void addPlacedBlockLocation(Location location) {
        this.placedBlockLocations.add(location);
    }

    public void removePlacedBlockLocation(Location location) {
	this.placedBlockLocations.remove(location);
    }
    
    public Set<UUID> getSpectators() {
        return this.spectators;
    }
    
    public Set<Integer> getRunnables() {
        return this.runnables;
    }
    
    public List<MatchTeam> getTeams() {
        return this.teams;
    }
    
    public UUID getMatchId() {
        return this.matchId;
    }
    
    public QueueType getType() {
        return this.type;
    }
    
    public Arena getArena() {
        return this.arena;
    }
    
    public Kit getKit() {
        return this.kit;
    }
    
    public boolean isRedrover() {
        return this.redrover;
    }
    
    public StandaloneArena getStandaloneArena() {
        return this.standaloneArena;
    }
    
    public MatchState getMatchState() {
        return this.matchState;
    }
    
    public int getWinningTeamId() {
        return this.winningTeamId;
    }
    
    public int getCountdown() {
        return this.countdown;
    }
    
    public void setStandaloneArena(final StandaloneArena standaloneArena) {
        this.standaloneArena = standaloneArena;
    }
    
    public void setMatchState(final MatchState matchState) {
        this.matchState = matchState;
    }
    
    public void setWinningTeamId(final int winningTeamId) {
        this.winningTeamId = winningTeamId;
    }
    
    public void setCountdown(final int countdown) {
        this.countdown = countdown;
    }
}
