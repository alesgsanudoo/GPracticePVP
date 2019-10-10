package me.groyteam.practice.cache;

import java.util.ArrayList;
import java.util.List;

import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.Practice;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class StatsMenu extends Menu {

    public StatsMenu(Player player) {
        super(player, "stats", "§8Estadísticas de "+player.getName(), 6);
    }

    @Override
    public void onOpen(InventoryOpenEvent p0) {
        this.update();
    }

    @Override
    public void onClose(InventoryCloseEvent p0) {}

    @Override
    public void onClick(InventoryClickEvent p0) {}

    @Override
    public void update() {
        PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(this.getPlayer().getUniqueId());
        this.setItem(53, new ItemBuilder2(Material.DIAMOND_SWORD).setTitle("§3§lVictorias Globales").addLore("§f"+ playerData.getRankedWins()));
        this.setItem(52, new ItemBuilder2(Material.GOLD_SWORD).setTitle("§3§lElo Global").addLore("§f"+ playerData.getGlobalRankedElo()));
        this.setItem(49, new ItemBuilder2(Material.BOOK).setTitle("§3§lDivisión Global").addLore("§f"+ playerData.getRankFromElo()));
        
        List<String> gapple = new ArrayList<>();
        gapple.add("§f");
        gapple.add("§2● §3Ganadas: §f"+ playerData.getWins("Gapple"));
        gapple.add("§2● §3Elo: §f"+ playerData.getElo("Gapple"));
        gapple.add("§2● §3Perdidas: §f"+ playerData.getLosses("Gapple"));
        gapple.add("§f");
        this.setItem(12, new ItemBuilder2(Material.GOLDEN_APPLE, 1, (short)1).setTitle("§a§lGapple").addLore(gapple));
        
        List<String> soup = new ArrayList<>();
        soup.add("§f");
        soup.add("§2● §3Ganadas: §f"+ playerData.getWins("Soup"));
        soup.add("§2● §3Elo: §f"+ playerData.getElo("Soup"));
        soup.add("§2● §3Perdidas: §f"+ playerData.getLosses("Soup"));
        soup.add("§f");
        this.setItem(13, new ItemBuilder2(Material.MUSHROOM_SOUP).setTitle("§a§lSoup").addLore(soup));
        
        List<String> nodebuff = new ArrayList<>();
        nodebuff.add("§f");
        nodebuff.add("§2● §3Ganadas: §f"+ playerData.getWins("NoDebuff"));
        nodebuff.add("§2● §3Elo: §f"+ playerData.getElo("NoDebuff"));
        nodebuff.add("§2● §3Perdidas: §f"+ playerData.getLosses("NoDebuff"));
        nodebuff.add("§f");
        this.setItem(14, new ItemBuilder2(Material.POTION, 1, (short)16421).setTitle("§a§lNoDebuff").addLore(nodebuff));
        
        List<String> debuff = new ArrayList<>();
        debuff.add("§f");
        debuff.add("§2● §3Ganadas: §f"+ playerData.getWins("Debuff"));
        debuff.add("§2● §3Elo: §f"+ playerData.getElo("Debuff"));
        debuff.add("§2● §3Perdidas: §f"+ playerData.getLosses("Debuff"));
        debuff.add("§f");
        this.setItem(20, new ItemBuilder2(Material.POTION, 1, (short)16388).setTitle("§a§lDebuff").addLore(debuff));
        
        List<String> sumo = new ArrayList<>();
        sumo.add("§f");
        sumo.add("§2● §3Ganadas: §f"+ playerData.getWins("Sumo"));
        sumo.add("§2● §3Elo: §f"+ playerData.getElo("Sumo"));
        sumo.add("§2● §3Perdidas: §f"+ playerData.getLosses("Sumo"));
        sumo.add("§f");
        this.setItem(21, new ItemBuilder2(Material.LEASH).setTitle("§a§lSumo").addLore(sumo));
        
        List<String> archer = new ArrayList<>();
        archer.add("§f");
        archer.add("§2● §3Ganadas: §f"+ playerData.getWins("Archer"));
        archer.add("§2● §3Elo: §f"+ playerData.getElo("Archer"));
        archer.add("§2● §3Perdidas: §f"+ playerData.getLosses("Archer"));
        archer.add("§f");
        this.setItem(22, new ItemBuilder2(Material.BOW).setTitle("§a§lArcher").addLore(archer));
        
        List<String> build = new ArrayList<>();
        build.add("§f");
        build.add("§2● §3Ganadas: §f"+ playerData.getWins("BuildUHC"));
        build.add("§2● §3Elo: §f"+ playerData.getElo("BuildUHC"));
        build.add("§2● §3Perdidas: §f"+ playerData.getLosses("BuildUHC"));
        build.add("§f");
        this.setItem(23, new ItemBuilder2(Material.LAVA_BUCKET).setTitle("§a§lBuildUHC").addLore(build));
        
        List<String> combo = new ArrayList<>();
        combo.add("§f");
        combo.add("§2● §3Ganadas: §f"+ playerData.getWins("Combo"));
        combo.add("§2● §3Elo: §f"+ playerData.getElo("Combo"));
        combo.add("§2● §3Perdidas: §f"+ playerData.getLosses("Combo"));
        combo.add("§f");
        this.setItem(24, new ItemBuilder2(Material.getMaterial(349), 1, (short)3).setTitle("§a§lCombo").addLore(combo));
        
        List<String> classic = new ArrayList<>();
        classic.add("§f");
        classic.add("§2● §3Ganadas: §f"+ playerData.getWins("Classic"));
        classic.add("§2● §3Elo: §f"+ playerData.getElo("Classic"));
        classic.add("§2● §3Perdidas: §f"+ playerData.getLosses("Classic"));
        classic.add("§f");
        this.setItem(31, new ItemBuilder2(Material.FISHING_ROD).setTitle("§a§lClassic").addLore(classic));
        
        this.setItem(45, new ItemBuilder2(Material.IRON_SWORD).setTitle("§3§lAsesinatos Globales").addLore("§f"+ getPlayer().getStatistic(Statistic.PLAYER_KILLS)));
        this.setItem(46, new ItemBuilder2(Material.GOLD_SWORD).setTitle("§3§lMuertes Globales").addLore("§f"+ getPlayer().getStatistic(Statistic.DEATHS)));
    }
    
}
