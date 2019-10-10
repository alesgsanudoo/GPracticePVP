package net.latinplay.practice.ffa.killstreak.impl;

import java.util.Arrays;
import java.util.List;
import net.latinplay.practice.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import net.latinplay.practice.ffa.killstreak.KillStreak;

public class DebuffKillStreak implements KillStreak
{
    private static final ItemStack SLOWNESS;
    private static final ItemStack POISON;
    
    static {
        SLOWNESS = new ItemStack(Material.POTION, 1, (short)16394);
        POISON = new ItemStack(Material.POTION, 1, (short)16388);
    }
    
    @Override
    public void giveKillStreak(final Player player) {
        PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, DebuffKillStreak.SLOWNESS.clone());
        PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, DebuffKillStreak.POISON.clone());
    }
    
    @Override
    public List<Integer> getStreaks() {
        return Arrays.<Integer>asList(7, 25);
    }
}
