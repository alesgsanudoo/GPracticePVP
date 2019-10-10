package me.groyteam.practice.board;

import org.bukkit.entity.Player;

public class Packs implements Packets {

    @Override
    public int verPing(Player player){
        return ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().ping;
    }
    
    
}
