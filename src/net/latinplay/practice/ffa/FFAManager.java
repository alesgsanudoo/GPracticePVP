package net.latinplay.practice.ffa;

import java.util.HashSet;
import java.util.HashMap;
import net.latinplay.practice.player.PlayerData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import net.latinplay.practice.player.PlayerState;
import net.latinplay.practice.ffa.killstreak.impl.GodAppleKillStreak;
import net.latinplay.practice.ffa.killstreak.impl.DebuffKillStreak;
import net.latinplay.practice.ffa.killstreak.impl.GappleKillStreak;
import org.bukkit.entity.Player;
import net.latinplay.practice.kit.Kit;
import net.latinplay.practice.CustomLocation;
import net.latinplay.practice.Practice;
import net.latinplay.practice.ffa.killstreak.KillStreak;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Item;
import java.util.Map;

public class FFAManager
{
    private final Map<Item, Long> itemTracker;
    private final Map<UUID, Integer> killStreakTracker;
    private final Set<KillStreak> killStreaks;
    private final Practice plugin;
    private final CustomLocation spawnPoint;
    private final Kit kit;
    
    public void addPlayer(final Player player) {
        if (this.killStreaks.isEmpty()) {
            this.killStreaks.add(new GappleKillStreak());
            this.killStreaks.add(new DebuffKillStreak());
            this.killStreaks.add(new GodAppleKillStreak());
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.FFA);
        player.teleport(this.spawnPoint.toBukkitLocation());
        player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aHas sido enviado a la arena de FFA.");
        this.kit.applyToPlayer(player);
        for (int i = 0; i < player.getInventory().getContents().length; ++i) {
            final ItemStack itemStack = player.getInventory().getContents()[i];
            if (itemStack != null && itemStack.getType() == Material.POTION) {
                player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
            }
        }
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
        for (final PlayerData data : this.plugin.getPlayerManager().getAllData()) {
            final Player player2 = this.plugin.getServer().getPlayer(data.getUniqueId());
            if (data.getPlayerState() == PlayerState.FFA) {
                player.showPlayer(player2);
                player2.showPlayer(player);
            }
            else {
                player.hidePlayer(player2);
                player2.hidePlayer(player);
            }
        }
    }
    
    public void removePlayer(final Player player) {
        for (final PlayerData data : this.plugin.getPlayerManager().getAllData()) {
            final Player player2 = this.plugin.getServer().getPlayer(data.getUniqueId());
            if (data.getPlayerState() == PlayerState.FFA) {
                player.hidePlayer(player2);
                player2.hidePlayer(player);
            }
        }
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }
    
    public Map<Item, Long> getItemTracker() {
        return this.itemTracker;
    }
    
    public Map<UUID, Integer> getKillStreakTracker() {
        return this.killStreakTracker;
    }
    
    public Set<KillStreak> getKillStreaks() {
        return this.killStreaks;
    }
    
    public FFAManager(final CustomLocation spawnPoint, final Kit kit) {
        this.itemTracker = new HashMap<Item, Long>();
        this.killStreakTracker = new HashMap<>();
        this.killStreaks = new HashSet<>();
        this.plugin = Practice.getInstance();
        this.spawnPoint = spawnPoint;
        this.kit = kit;
    }
}
