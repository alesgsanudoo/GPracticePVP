package net.latinplay.practice.events.sumo;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import java.util.Arrays;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.stream.Collectors;
import net.latinplay.practice.events.EventPlayer;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.scheduler.BukkitTask;
import net.latinplay.practice.player.PlayerData;
import net.latinplay.practice.Practice;
import org.bukkit.Bukkit;
import net.latinplay.practice.util.PlayerUtil;
import org.bukkit.entity.Player;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;
import java.util.Collections;
import net.latinplay.practice.CustomLocation;
import java.util.List;
import net.latinplay.practice.events.EventCountdownTask;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.Map;
import net.latinplay.practice.events.PracticeEvent;

public class SumoEvent extends PracticeEvent<SumoPlayer>
{
    private final Map<UUID, SumoPlayer> players;
    final HashSet<String> fighting;
    private final SumoCountdownTask countdownTask;
    private WaterCheckTask waterCheckTask;
    
    public SumoEvent() {
        super("Sumo");
        this.players = new HashMap<>();
        this.fighting = new HashSet<>();
        this.countdownTask = new SumoCountdownTask(this);
    }
    
    @Override
    public Map<UUID, SumoPlayer> getPlayers() {
        return this.players;
    }
    
    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }
    
    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getSumoLocation());
    }
    
    @Override
    public void onStart() {
        (this.waterCheckTask = new WaterCheckTask()).runTaskTimer(this.getPlugin(), 0L, 10L);
        this.selectPlayers();
    }
    
    @Override
    public Consumer<Player> onJoin() {
        return player -> this.players.put(player.getUniqueId(), new SumoPlayer(player, player.getUniqueId(), this));
    }
    
    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            String string;
            SumoPlayer data = this.getPlayer(player);
            if (data != null && data.getFighting() != null) {
                if (data.getState() == SumoPlayer.SumoState.FIGHTING || data.getState() == SumoPlayer.SumoState.PREPARING) {
                    SumoPlayer killerData = data.getFighting();
                    Player killer = this.getPlugin().getServer().getPlayer(killerData.getUuid());
                    data.getFightTask().cancel();
                    killerData.getFightTask().cancel();
                    PlayerData playerData = this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId());
                    if (playerData != null) {
                        playerData.setSumoEventLosses(playerData.getSumoEventLosses() + 1);
                    }
                    data.setState(SumoPlayer.SumoState.ELIMINATED);
                    killerData.setState(SumoPlayer.SumoState.WAITING);
                    PlayerUtil.clearPlayer(player);
                    this.getPlugin().getPlayerManager().giveLobbyItems(player);
                    PlayerUtil.clearPlayer(killer);
                    this.getPlugin().getPlayerManager().giveLobbyItems(killer);
                    if (this.getSpawnLocations().size() == 1) {
                        player.teleport(this.getSpawnLocations().get(0).toBukkitLocation());
                        killer.teleport(this.getSpawnLocations().get(0).toBukkitLocation());
                    }
                    StringBuilder sb = new StringBuilder().append("§fEl jugador ").append("§3").append(player.getName()).append(" §fha sido eliminado");
                    if (killer == null) {
                        string = ".";
                    }
                    else {
                        string = " por §3" + killer.getName() + "§f.";
                    }
                    this.sendMessage(sb.append(string).toString());
                    this.getPlugin().getServer().getScheduler().runTaskLater(this.getPlugin(), () -> this.selectPlayers(), 60L);
                }
            }
        };
    }
    
    private CustomLocation[] getSumoLocations() {
        final CustomLocation[] array = { this.getPlugin().getSpawnManager().getSumoFirst(), this.getPlugin().getSpawnManager().getSumoSecond() };
        return array;
    }
    
    private void selectPlayers() {
        if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
            final Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoState.WAITING).get(0));
            final PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
            winnerData.setSumoEventWins(winnerData.getSumoEventWins() + 1);
            for (int i = 0; i <= 2; ++i) {
                for(int o = 0; o < this.players.size(); o++) {
                }
            }
            final String announce = "§a§l¡FELICIDADES! §fEl jugador §3" + winner.getName() + " §fha ganado.";
            Bukkit.broadcastMessage(announce);
            this.fighting.clear();
            this.end();
            return;
        }
        final Player picked1 = this.getRandomPlayer();
        final Player picked2 = this.getRandomPlayer();
        if (picked1 == null || picked2 == null) {
            this.selectPlayers();
            return;
        }
        this.sendMessage("§3§lSeleccionando a 2 jugadores aleatorios...");
        this.fighting.clear();
        final SumoPlayer picked1Data = this.getPlayer(picked1);
        final SumoPlayer picked2Data = this.getPlayer(picked2);
        picked1Data.setFighting(picked2Data);
        picked2Data.setFighting(picked1Data);
        this.fighting.add(picked1.getName());
        this.fighting.add(picked2.getName());
        PlayerUtil.clearPlayer(picked1);
        PlayerUtil.clearPlayer(picked2);
        picked1.teleport(this.getSumoLocations()[0].toBukkitLocation());
        picked2.teleport(this.getSumoLocations()[1].toBukkitLocation());
        for (final Player other : this.getBukkitPlayers()) {
            if (other != null) {
                other.showPlayer(picked1);
                other.showPlayer(picked2);
            }
        }
        for (final UUID spectatorUUID : this.getPlugin().getEventManager().getSpectators().keySet()) {
            final Player spectator = Bukkit.getPlayer(spectatorUUID);
            if (spectatorUUID != null) {
                spectator.showPlayer(picked1);
                spectator.showPlayer(picked2);
            }
        }
        picked1.showPlayer(picked2);
        picked2.showPlayer(picked1);
        this.sendMessage("§3§lCOMENZANDO! §fSe ha escogido a estos 2 jugadores, por lo que va comnzar el combate. §8(§3" + picked1.getName() + " §fvs §3" + picked2.getName() + "§8)§f.");
        final BukkitTask task = new SumoFightTask(picked1, picked2, picked1Data, picked2Data).runTaskTimer(this.getPlugin(), 0L, 20L);
        picked1Data.setFightTask(task);
        picked2Data.setFightTask(task);
    }
    
    private Player getRandomPlayer() {
        if (this.getByState(SumoPlayer.SumoState.WAITING).isEmpty()) {
            return null;
        }
        final List<UUID> waiting = this.getByState(SumoPlayer.SumoState.WAITING);
        Collections.shuffle(waiting);
        final UUID uuid = waiting.get(ThreadLocalRandom.current().nextInt(waiting.size()));
        this.getPlayer(uuid).setState(SumoPlayer.SumoState.PREPARING);
        return this.getPlugin().getServer().getPlayer(uuid);
    }
    
    public List<UUID> getByState(final SumoPlayer.SumoState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }
    
    public HashSet<String> getFighting() {
        return this.fighting;
    }
    
    public WaterCheckTask getWaterCheckTask() {
        return this.waterCheckTask;
    }
    
    public class SumoFightTask extends BukkitRunnable
    {
        private final Player player;
        private final Player other;
        private final SumoPlayer playerSumo;
        private final SumoPlayer otherSumo;
        private int time;
        
        @Override
        public void run() {
            if (this.player == null || this.other == null || !this.player.isOnline() || !this.other.isOnline()) {
                this.cancel();
                return;
            }
            if (this.time == 90) {
                PlayerUtil.sendMessage("§fEl combate comienza en §3" + 3 + "§f segundos...", this.player, this.other);
            }
            else if (this.time == 89) {
                PlayerUtil.sendMessage("§fEl combate comienza en §3" + 2 + "§f segundos...", this.player, this.other);
            }
            else if (this.time == 88) {
                PlayerUtil.sendMessage("§fEl combate comienza en §3" + 1 + "§f segundos...", this.player, this.other);
            }
            else if (this.time == 87) {
                PlayerUtil.sendMessage("§3§l¡BUENA SUERTE! §fQue comience el combate y que gane el mejor.", this.player, this.other);
                this.otherSumo.setState(SumoPlayer.SumoState.FIGHTING);
                this.playerSumo.setState(SumoPlayer.SumoState.FIGHTING);
            }
            else if (this.time <= 0) {
                final List<Player> players = Arrays.asList(this.player, this.other);
                final Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));
                players.stream().filter(pl -> !pl.equals(winner)).forEach(pl -> SumoEvent.this.onDeath().accept(pl));
                this.cancel();
                return;
            }
            if (Arrays.asList(30, 25, 20, 15, 10).contains(this.time)) {
                PlayerUtil.sendMessage("§fEl combate termina en §3" + this.time + "§f segundos...", this.player, this.other);
            }
            else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
                PlayerUtil.sendMessage("§fEl combate termina en §3" + this.time + "§f segundos...", this.player, this.other);
            }
            --this.time;
        }
        
        public Player getPlayer() {
            return this.player;
        }
        
        public Player getOther() {
            return this.other;
        }
        
        public SumoPlayer getPlayerSumo() {
            return this.playerSumo;
        }
        
        public SumoPlayer getOtherSumo() {
            return this.otherSumo;
        }
        
        public int getTime() {
            return this.time;
        }
        
        public SumoFightTask(final Player player, final Player other, final SumoPlayer playerSumo, final SumoPlayer otherSumo) {
            this.time = 90;
            this.player = player;
            this.other = other;
            this.playerSumo = playerSumo;
            this.otherSumo = otherSumo;
        }
    }
    
    public class WaterCheckTask extends BukkitRunnable
    {
        @Override
        public void run() {
            if (SumoEvent.this.getPlayers().size() <= 1) {
                return;
            }
            SumoEvent.this.getBukkitPlayers().forEach(player -> {
                if (SumoEvent.this.getPlayer(player) == null || SumoEvent.this.getPlayer(player).getState() == SumoPlayer.SumoState.FIGHTING) {
                    Block legs = player.getLocation().getBlock();
                    Block head = legs.getRelative(BlockFace.UP);
                    if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                        SumoEvent.this.onDeath().accept(player);
                    }
                }
            });
        }
    }
}
