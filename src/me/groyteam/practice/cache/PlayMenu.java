package me.groyteam.practice.cache;

import java.util.ArrayList;
import java.util.List;

import me.groyteam.practice.party.Party;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.Practice;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class PlayMenu extends Menu{

    public PlayMenu(Player player) {
        super(player, "playmenu", "§8Menu de ArenaPvP", 4);
    }

    @Override
    public void onOpen(InventoryOpenEvent p0) {
        this.update();
    }

    @Override
    public void onClose(InventoryCloseEvent p0) {}

    @Override
    public void onClick(InventoryClickEvent event) {
        final Player player = getPlayer();
        final PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        final Party party = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());
        if (event.getCurrentItem().getType() == Material.DIAMOND_SWORD) {
            if(playerData.getRankeds() == 0) {
                player.sendMessage("§c§l¡NO TE QUEDAN RANKEDS! §fEn el día de hoy no te quedan rankeds. Si quieres más rankeds diarios compra tu rango en &3tienda.groyland.net&f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 0) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §315 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 1) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §314 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 2) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §313 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 3) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §312 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 4) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §311 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 5) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §310 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 6) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §39 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 7) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §38 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 8) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §37 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 9) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §36 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 10) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §35 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 11) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §34 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 12) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §33 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 13) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §32 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 14) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §31 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if(player.getStatistic(Statistic.PLAYER_KILLS) == 15) {
                player.sendMessage("§c§l¡NO PUEDES! §fPrimero deberas matar a §315 §fpersonas en el §3§lModo UnRanked§f, antes de jugar al §3§lModo Ranked§f.");
                return;
            }
            if (party != null && !Practice.getInstance().getPartyManager().isLeader(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo puedes hacer esto mientras estás en otros modos de juego.");
                return;
            }
            player.closeInventory();
            player.openInventory(Practice.getInstance().getInventoryManager().getRankedInventory().getCurrentPage());
        }
        if (event.getCurrentItem().getType() == Material.PAPER) {
            player.closeInventory();
            player.openInventory(MenuListener.getPlayerMenu(player, "stats").getInventory());
        }
        if (event.getCurrentItem().getType() == Material.IRON_SWORD) {
            player.closeInventory();
            player.openInventory(Practice.getInstance().getInventoryManager().getUnrankedInventory().getCurrentPage());
        }
        if (event.getCurrentItem().getType() == Material.DIAMOND_CHESTPLATE) {
            player.closeInventory();
            player.openInventory(Practice.getInstance().getInventoryManager().getEditorInventory().getCurrentPage());
        }
        if (event.getCurrentItem().getType() == Material.REDSTONE_COMPARATOR) {
            player.closeInventory();
            player.openInventory(playerData.getOptions().getInventory());
        }
    }

    @Override
    public void update() {
        final Player player = getPlayer();
        final PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        List<String> ranked = new ArrayList<>();
        ranked.add("");
        ranked.add("§fJuega al §3§lModo Ranked");
        ranked.add("§fpara llegar a estar entre,");
        ranked.add("§flos mejores jugadores, ");
        ranked.add("§fe intentar conseguir un rango");
        ranked.add("§falto en la §3§lDivisión Global§f.");
        ranked.add("");
        ranked.add("§2● §fTienes: §3" + (player.hasPermission("practice.king") ? "§3Ilimitadas" : playerData.getRankeds()));
        ranked.add("");
        ranked.add("§aClick para jugar.");
        this.setItem(11, new ItemBuilder2(Material.DIAMOND_SWORD).setTitle("§fJugar a §3§lModo Ranked").setLore(ranked));
        List<String> unranked = new ArrayList<>();
        unranked.add("");
        unranked.add("§fJuega al §3§lModo UnRanked");
        unranked.add("§fy entrena para jugar");
        unranked.add("§fe intentar también ganar en el");
        unranked.add("§f§3§lModo Ranked§f.");
        unranked.add("");
        unranked.add("§aClick para jugar.");
        unranked.add("");
        this.setItem(13, new ItemBuilder2(Material.IRON_SWORD).setTitle("§fJugar a §3§lModo UnRanked").setLore(unranked));

        List<String> kit = new ArrayList<>();
        kit.add("§f");
        kit.add("§fPersonaliza tus §3kits");
        kit.add("§fa tu gusto para que sea más");
        kit.add("§fcomádo al jugar partidas.");
        kit.add("§f");
        kit.add("§aClick para editar un kit.");
        kit.add("");
        this.setItem(15, new ItemBuilder2(Material.DIAMOND_CHESTPLATE).setTitle("§fEditar tus §3§lkits").setLore(kit));


        List<String> opciones = new ArrayList<>();
        opciones.add("§f");
        opciones.add("§aClick para personalizar.");
        opciones.add("");
        this.setItem(35, new ItemBuilder2(Material.REDSTONE_COMPARATOR).setTitle("§fOpciones de §3§lArenaPvP").setLore(opciones));

        List<String> stats = new ArrayList<>();
        stats.add("§f");
        stats.add("§3¿Quieres ver tus estadísitas?");
        stats.add("§f");
        stats.add("§fHaz click aquí para ver tus");
        stats.add("§festadisticas de arenapvp.");
        stats.add("");
        stats.add("§f");
        stats.add("§3¿Quieres ver las estadísticas de otro jugador?");
        stats.add("§f");
        stats.add("§fSimplemente, usa el comando");
        stats.add("§3/stats (nombre) §fpara ver");
        stats.add("§flas estadísticas de otro jugador.");
        stats.add("§f");
        stats.add("§aClick ver estadísticas.");
        this.setItem(27, new ItemBuilder2(Material.PAPER).setTitle("§fTus estadisticas").setLore(stats));

    }

}

