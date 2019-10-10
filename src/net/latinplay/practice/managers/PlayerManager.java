package net.latinplay.practice.managers;

import java.text.SimpleDateFormat;
import net.latinplay.practice.util.PlayerUtil;
import org.bukkit.inventory.ItemStack;
import net.latinplay.practice.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import java.util.Collection;
import java.util.Date;
import org.bukkit.Bukkit;
import java.util.List;
import net.latinplay.practice.file.Config;
import org.bukkit.plugin.Plugin;
import net.latinplay.practice.player.PlayerState;
import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;
import net.latinplay.practice.player.PlayerData;
import java.util.UUID;
import java.util.Map;
import java.util.Set;
import net.latinplay.practice.Practice;
import net.latinplay.practice.cache.HostMenu;
import net.latinplay.practice.cache.StatsMenu;
import net.latinplay.practice.events.sumo.SumoEvent;
import net.latinplay.practice.kit.Kit;
import net.latinplay.practice.kit.PlayerKit;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerManager
{
    private final Practice plugin;
    private final Map<UUID, PlayerData> playerData;

    public PlayerManager() {
        this.plugin = Practice.getInstance();
        this.playerData = new ConcurrentHashMap<>();
    }

    public void createPlayerData(Player player) {
        PlayerData data = new PlayerData(player, player.getUniqueId());
        this.playerData.put(data.getUniqueId(), data);
        this.loadData(data);
        new StatsMenu(player);
        new HostMenu(player);
    }

    private void loadData(PlayerData playerData) {
        Config config = new Config("/players/" + playerData.getUniqueId().toString(), this.plugin);
        if(!config.getConfig().isSet("data")) {
            config.getConfig().set("stats.totalwins", 0);
            config.getConfig().set("stats.totalloser", 0);
            for(Kit kit : Practice.getInstance().getKitManager().getKits()) {
                config.getConfig().set("stats.elo."+kit.getName(), 800);
                config.getConfig().set("stats.wins."+kit.getName(), 0);
                config.getConfig().set("stats.loss."+kit.getName(), 0);

                playerData.setElo(kit.getName(), config.getConfig().getInt("stats.elo."+kit.getName()));
                playerData.setWins(kit.getName(), config.getConfig().getInt("stats.wins."+kit.getName()));
                playerData.setLosses(kit.getName(), config.getConfig().getInt("stats.loss."+kit.getName()));
            }
            config.getConfig().set("rankeds", playerData.rankRanked());
            config.getConfig().set("data", playerData.getPlayer().getUniqueId().toString());
            String s = new SimpleDateFormat("dd").format(new Date());
            config.getConfig().set("timedate", s);
            playerData.setPlayerState(PlayerState.SPAWN);
            config.save();
        } else {
            for(Kit kit : Practice.getInstance().getKitManager().getKits()) {
                playerData.setElo(kit.getName(), config.getConfig().getInt("stats.elo."+kit.getName()));
                playerData.setWins(kit.getName(), config.getConfig().getInt("stats.wins."+kit.getName()));
                playerData.setLosses(kit.getName(), config.getConfig().getInt("stats.loss."+kit.getName()));
            }
            if(config.getConfig().isSet("playerkits")) {
                this.plugin.getKitManager().getKits().forEach(kit -> {
                    if(config.getConfig().isSet("playerkits."+kit.getName())) {
                        Map<Integer, PlayerKit> playerKits = playerData.getPlayerKits(kit.getName());
                        Set<String> section = config.getConfig().getConfigurationSection("playerkits." + kit.getName()).getKeys(false);
                        for(String key : section) {
                            String name = kit.getName();
                            List<ItemStack> i = (List<ItemStack>)config.getConfig().get("playerkits." + name + "." + key + ".contents");
                            ItemStack[] contents = i.toArray(new ItemStack[0]);
                            PlayerKit pkit = new PlayerKit(name, Integer.parseInt(key), contents, config.getConfig().getString("playerkits." + kit.getName() + "." + key + ".displayName"));
                            playerData.addPlayerKit(Integer.parseInt(key), pkit);
                        }
                    }
                });
            }
            String s = new SimpleDateFormat("dd").format(new Date());
            String a = config.getConfig().getString("timedate");
            if(a == null ? s != null : !a.equals(s)) {
                playerData.setRankeds(playerData.rankRanked());
                config.getConfig().set("timedate", s);
                config.save();
            } else {
                playerData.setRankeds(config.getConfig().getInt("rankeds"));
            }
            playerData.setPlayerState(PlayerState.SPAWN);
        }
    }

    public void removePlayerData(UUID uuid) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.saveData(this.playerData.get(uuid));
            this.playerData.remove(uuid);
        });
    }

    public void saveData(PlayerData playerData) {
        if (playerData == null) {
            return;
        }
        Config config = new Config("/players/" + playerData.getUniqueId().toString(), this.plugin);
        Config topwins = new Config("topwins", this.plugin);
        Config topelo = new Config("topelo", this.plugin);
        config.getConfig().set("stats.totalwins", playerData.getRankedWins());
        config.getConfig().set("stats.totalloser", playerData.getRankedLosses());
        for(Kit kit : Practice.getInstance().getKitManager().getKits()) {
            config.getConfig().set("stats.elo."+kit.getName(), playerData.getElo(kit.getName()));
            config.getConfig().set("stats.wins."+kit.getName(), playerData.getWins(kit.getName()));
            config.getConfig().set("stats.loss."+kit.getName(), playerData.getLosses(kit.getName()));
        }
        this.plugin.getKitManager().getKits().forEach(kit -> {
            Map<Integer, PlayerKit> playerKits = playerData.getPlayerKits(kit.getName());

            if (playerKits != null) {
                playerKits.forEach((key, value) -> {
                    config.getConfig().set("playerkits." + kit.getName() + "." + key + ".displayName", value.getDisplayName());
                    config.getConfig().set("playerkits." + kit.getName() + "." + key + ".contents", value.getContents());
                });
            }
        });
        config.getConfig().set("rankeds", playerData.getRankeds());
        config.save();

        if(playerData.getGlobalRankedElo() >= 1001) {
            topelo.getConfig().set(playerData.getPlayer().getName(), playerData.getGlobalRankedElo());
        }
        if(config.getConfig().getInt("stats.totalwins") >= 1) {
            topwins.getConfig().set(playerData.getPlayer().getName(), playerData.getRankedWins());
        }

        topwins.save();
        topelo.save();
    }

    public Collection<PlayerData> getAllData() {
        return this.playerData.values();
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerData.get(uuid);
    }

    public void giveLobbyItems(Player player) {
        boolean inParty = this.plugin.getPartyManager().getParty(player.getUniqueId()) != null;
        boolean inTournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null;
        boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;
        boolean isRematching = this.plugin.getMatchManager().isRematching(player.getUniqueId());
        ItemStack[] items = this.plugin.getItemManager().getSpawnItems();
        if (inTournament) {
            items = this.plugin.getItemManager().getTournamentItems();
        }
        else if (inEvent) {
            items = this.plugin.getItemManager().getEventItems();
        }
        else if (inParty) {
            items = this.plugin.getItemManager().getPartyItems();
        }
        player.getInventory().setContents(items);
        if (isRematching && !inParty && !inTournament && !inEvent) {
            player.getInventory().setItem(2, ItemUtil.createItem(Material.EMERALD, String.valueOf(ChatColor.BLUE.toString()) + ChatColor.BOLD + "Rematch"));
        }
        player.updateInventory();
    }

    public void sendToSpawnEventFinish(SumoEvent sumo, Player player) {
        PlayerData pdata = this.getPlayerData(player.getUniqueId());
        pdata.setPlayerState(PlayerState.SPAWN);
        PlayerUtil.clearPlayer(player);
        this.plugin.getEventManager().getEventWorld().getPlayers().remove(player);
        sumo.getPlayers().remove(player.getUniqueId());
        this.giveLobbyItems(player);
        if (!player.isOnline()) {
            return;
        }
        this.plugin.getServer().getOnlinePlayers().forEach(p -> {
        });
        player.teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
        this.giveLobbyItems(player);
    }

    public void sendToSpawnAndReset(Player player) {
        PlayerData pdata = this.getPlayerData(player.getUniqueId());
        pdata.setPlayerState(PlayerState.SPAWN);
        PlayerUtil.clearPlayer(player);
        this.giveLobbyItems(player);
        if (!player.isOnline()) {
            return;
        }
        this.plugin.getServer().getOnlinePlayers().forEach(p -> {
        });
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
            }
        });
        player.getInventory().clear();
        this.giveLobbyItems(player);
    }
}
