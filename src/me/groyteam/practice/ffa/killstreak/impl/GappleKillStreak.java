package me.groyteam.practice.ffa.killstreak.impl;

import java.util.Arrays;
import java.util.List;

import me.groyteam.practice.ffa.killstreak.KillStreak;
import me.groyteam.practice.util.PlayerUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GappleKillStreak implements KillStreak
{
    @Override
    public void giveKillStreak(final Player player) {
        PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, new ItemStack(Material.GOLDEN_APPLE, 3));
    }
    
    @Override
    public List<Integer> getStreaks() {
        return Arrays.<Integer>asList(3, 15);
    }
}
