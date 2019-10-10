package me.groyteam.practice.events.redrover;

import me.groyteam.practice.events.EventCountdownTask;
import me.groyteam.practice.events.EventPlayer;
import me.groyteam.practice.events.PracticeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import org.bukkit.scheduler.BukkitTask;
import me.groyteam.practice.util.PlayerUtil;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.StringJoiner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;
import java.util.Collections;
import me.groyteam.practice.CustomLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;

public class RedroverEvent extends PracticeEvent<RedroverPlayer>
{
    private final Map<UUID, RedroverPlayer> players;
    private final List<UUID> blueTeam;
    private final List<UUID> redTeam;
    UUID streakPlayer;
    final List<UUID> fighting;
    private RedroverGameTask gameTask;
    private final RedroverCountdownTask countdownTask;
    
    public RedroverEvent() {
        super("Redrover");
        this.players = new HashMap<UUID, RedroverPlayer>();
        this.blueTeam = new ArrayList<UUID>();
        this.redTeam = new ArrayList<UUID>();
        this.streakPlayer = null;
        this.fighting = new ArrayList<UUID>();
        this.gameTask = null;
        this.countdownTask = new RedroverCountdownTask(this);
    }
    
    @Override
    public Map<UUID, RedroverPlayer> getPlayers() {
        return this.players;
    }
    
    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }
    
    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.<CustomLocation>singletonList(this.getPlugin().getSpawnManager().getRedroverLocation());
    }
    
    @Override
    public void onStart() {
        (this.gameTask = new RedroverGameTask()).runTaskTimerAsynchronously((Plugin)this.getPlugin(), 0L, 20L);
        this.fighting.clear();
        this.redTeam.clear();
        this.blueTeam.clear();
        this.generateTeams();
    }
    
    @Override
    public Consumer<Player> onJoin() {
        return player -> this.players.put(player.getUniqueId(), new RedroverPlayer(player.getUniqueId(), this));
    }
    
    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            RedroverPlayer data = this.getPlayer(player);
            String string;
            if (data != null) {
                if (data.getState() == RedroverPlayer.RedroverState.FIGHTING || data.getState() == RedroverPlayer.RedroverState.PREPARING) {
                    if (data.getFightTask() != null) {
                        data.getFightTask().cancel();
                    }
                    if (data.getFightPlayer() != null && data.getFightPlayer().getFightTask() != null) {
                        data.getFightPlayer().getFightTask().cancel();
                    }
                    this.getPlayers().remove(player.getUniqueId());
                    StringBuilder sb = new StringBuilder().append(ChatColor.YELLOW).append("(Event) ").append(ChatColor.RED).append(player.getName()).append(ChatColor.GRAY).append(" ha sido eliminado");
                    if (Bukkit.getPlayer(data.getFightPlayer().getUuid()) == null) {
                        string = ".";
                    }
                    else {
                        string = " by " + ChatColor.GREEN + Bukkit.getPlayer(data.getFightPlayer().getUuid()).getName();
                    }
                    this.sendMessage(sb.append(string).toString());
                    this.getPlugin().getServer().getScheduler().runTask((Plugin)this.getPlugin(), () -> {
                        this.getPlugin().getPlayerManager().sendToSpawnAndReset(player);
                        if (this.getPlayers().size() >= 2) {
                            this.getPlugin().getEventManager().addSpectatorRedrover(player, this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()), this);
                        }
                        return;
                    });
                    this.fighting.remove(player.getUniqueId());
                    this.redTeam.remove(player.getUniqueId());
                    this.blueTeam.remove(player.getUniqueId());
                    this.prepareNextMatch();
                }
            }
        };
    }
    
    private CustomLocation[] getGameLocations() {
        final CustomLocation[] array = { this.getPlugin().getSpawnManager().getRedroverFirst(), this.getPlugin().getSpawnManager().getRedroverSecond() };
        return array;
    }
    
    private void prepareNextMatch() {
        if (this.blueTeam.isEmpty() || this.redTeam.isEmpty()) {
            final List<UUID> winnerTeam = this.getWinningTeam();
            String winnerTeamName = String.valueOf(ChatColor.WHITE.toString()) + ChatColor.BOLD + "Tie";
            if (this.redTeam.size() > this.blueTeam.size()) {
                winnerTeamName = String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD + "RED";
            }
            else if (this.blueTeam.size() > this.redTeam.size()) {
                winnerTeamName = String.valueOf(ChatColor.BLUE.toString()) + ChatColor.BOLD + "BLUE";
            }
            final StringJoiner winnerJoiner = new StringJoiner(", ");
            if (winnerTeam != null && winnerTeam.size() > 0) {
                for (final UUID winner : winnerTeam) {
                    final Player player = this.getPlugin().getServer().getPlayer(winner);
                    if (player != null) {
                        winnerJoiner.add(player.getName());
                        this.fighting.remove(player.getUniqueId());
                    }
                }
            }
            for (int i = 0; i <= 2; ++i) {
                final String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winnerTeamName + ((winnerJoiner.length() == 0) ? "" : ("\n" + ChatColor.YELLOW + "(Event) " + ChatColor.GRAY + winnerJoiner.toString()));
                Bukkit.broadcastMessage(announce);
            }
            this.gameTask.cancel();
            this.end();
            return;
        }
        RedroverPlayer redPlayer = this.getPlayer(this.redTeam.get(ThreadLocalRandom.current().nextInt(this.redTeam.size())));
        RedroverPlayer bluePlayer = this.getPlayer(this.blueTeam.get(ThreadLocalRandom.current().nextInt(this.blueTeam.size())));
        if (this.fighting.size() == 1 && this.redTeam.contains(this.fighting.get(0))) {
            redPlayer = this.getPlayer(this.fighting.get(0));
            this.streakPlayer = redPlayer.getUuid();
        }
        else if (this.fighting.size() == 1 && this.blueTeam.contains(this.fighting.get(0))) {
            bluePlayer = this.getPlayer(this.fighting.get(0));
            this.streakPlayer = bluePlayer.getUuid();
        }
        this.fighting.clear();
        this.fighting.addAll(Arrays.<UUID>asList(redPlayer.getUuid(), bluePlayer.getUuid()));
        final Player picked1 = this.getPlugin().getServer().getPlayer(redPlayer.getUuid());
        final Player picked2 = this.getPlugin().getServer().getPlayer(bluePlayer.getUuid());
        redPlayer.setState(RedroverPlayer.RedroverState.PREPARING);
        bluePlayer.setState(RedroverPlayer.RedroverState.PREPARING);
        final BukkitTask task = new RedroverFightTask(picked1, picked2, redPlayer, bluePlayer).runTaskTimer((Plugin)this.getPlugin(), 0L, 20L);
        redPlayer.setFightPlayer(bluePlayer);
        bluePlayer.setFightPlayer(redPlayer);
        redPlayer.setFightTask(task);
        bluePlayer.setFightTask(task);
        this.getPlugin().getServer().getScheduler().runTask((Plugin)this.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                final Player[] players = { picked1, picked2 };
                Player[] array;
                for (int length = (array = players).length, i = 0; i < length; ++i) {
                    final Player player = array[i];
                    if (RedroverEvent.this.streakPlayer == null || RedroverEvent.this.streakPlayer != player.getUniqueId()) {
                        PlayerUtil.clearPlayer(player);
                        RedroverEvent.this.getPlugin().getKitManager().getKit("NoDebuff").applyToPlayer(player);
                        player.updateInventory();
                    }
                }
                picked1.teleport(RedroverEvent.this.getGameLocations()[0].toBukkitLocation());
                picked2.teleport(RedroverEvent.this.getGameLocations()[1].toBukkitLocation());
            }
        });
        this.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY.toString() + "Upcoming Match: " + ChatColor.RED + picked1.getName() + ChatColor.GRAY + " vs. " + ChatColor.BLUE + picked2.getName() + ChatColor.GRAY + ".");
    }
    
    private void generateTeams() {
        final ArrayList<UUID> players = (ArrayList<UUID>)Lists.newArrayList((Iterable)this.players.keySet());
        this.redTeam.addAll(players.subList(0, players.size() / 2 + players.size() % 2));
        this.blueTeam.addAll(players.subList(players.size() / 2 + players.size() % 2, players.size()));
        for (final UUID uuid : this.blueTeam) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY.toString() + "You have been added to the " + ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE" + ChatColor.GRAY + " Team.");
            }
        }
        for (final UUID uuid : this.redTeam) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY.toString() + "You have been added to the " + ChatColor.RED.toString() + ChatColor.BOLD + "RED" + ChatColor.GRAY + " Team.");
            }
        }
    }
    
    private List<UUID> getWinningTeam() {
        if (this.redTeam.size() > this.blueTeam.size()) {
            return this.redTeam;
        }
        if (this.blueTeam.size() > this.redTeam.size()) {
            return this.blueTeam;
        }
        return null;
    }
    
    public List<UUID> getByState(final RedroverPlayer.RedroverState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }
    
    public List<UUID> getBlueTeam() {
        return this.blueTeam;
    }
    
    public List<UUID> getRedTeam() {
        return this.redTeam;
    }
    
    public UUID getStreakPlayer() {
        return this.streakPlayer;
    }
    
    public List<UUID> getFighting() {
        return this.fighting;
    }
    
    public RedroverGameTask getGameTask() {
        return this.gameTask;
    }
    
    public class RedroverFightTask extends BukkitRunnable
    {
        private final Player player;
        private final Player other;
        private final RedroverPlayer redroverPlayer;
        private final RedroverPlayer redroverOther;
        private int time;
        
        public void run() {
            if (this.player == null || this.other == null || !this.player.isOnline() || !this.other.isOnline()) {
                this.cancel();
                return;
            }
            if (this.time == 180) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.GREEN + 3 + ChatColor.YELLOW + "...", this.player, this.other);
            }
            else if (this.time == 179) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.GREEN + 2 + ChatColor.YELLOW + "...", this.player, this.other);
            }
            else if (this.time == 178) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.GREEN + 1 + ChatColor.YELLOW + "...", this.player, this.other);
            }
            else if (this.time == 177) {
                PlayerUtil.sendMessage(ChatColor.GREEN + "The match has started, good luck!", this.player, this.other);
                this.redroverOther.setState(RedroverPlayer.RedroverState.FIGHTING);
                this.redroverPlayer.setState(RedroverPlayer.RedroverState.FIGHTING);
            }
            else if (this.time <= 0) {
                final List<Player> players = Arrays.<Player>asList(this.player, this.other);
                final Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));
                players.stream().filter(pl -> !pl.equals(winner)).forEach(pl -> RedroverEvent.this.onDeath().accept(pl));
                this.cancel();
                return;
            }
            if (Arrays.<Integer>asList(30, 25, 20, 15, 10).contains(this.time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match ends in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", this.player, this.other);
            }
            else if (Arrays.<Integer>asList(5, 4, 3, 2, 1).contains(this.time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match is ending in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", this.player, this.other);
            }
            --this.time;
        }
        
        public Player getPlayer() {
            return this.player;
        }
        
        public Player getOther() {
            return this.other;
        }
        
        public RedroverPlayer getRedroverPlayer() {
            return this.redroverPlayer;
        }
        
        public RedroverPlayer getRedroverOther() {
            return this.redroverOther;
        }
        
        public int getTime() {
            return this.time;
        }
        
        public RedroverFightTask(final Player player, final Player other, final RedroverPlayer redroverPlayer, final RedroverPlayer redroverOther) {
            this.time = 180;
            this.player = player;
            this.other = other;
            this.redroverPlayer = redroverPlayer;
            this.redroverOther = redroverOther;
        }
    }
    
    public class RedroverGameTask extends BukkitRunnable
    {
        private int time;
        
        public void run() {
            if (this.time == 1200) {
                RedroverEvent.this.prepareNextMatch();
            }
            if (Arrays.<Integer>asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game ends in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", RedroverEvent.this.getBukkitPlayers());
            }
            else if (Arrays.<Integer>asList(5, 4, 3, 2, 1).contains(this.time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game is ending in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", RedroverEvent.this.getBukkitPlayers());
            }
            --this.time;
        }
        
        public int getTime() {
            return this.time;
        }
        
        public void setTime(final int time) {
            this.time = time;
        }
        
        public RedroverGameTask() {
            this.time = 1200;
        }
    }
}
