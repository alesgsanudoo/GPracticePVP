package net.latinplay.practice.cache;

import java.util.ArrayList;
import java.util.List;
import net.latinplay.practice.Practice;
import net.latinplay.practice.events.EventState;
import net.latinplay.practice.util.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class HostMenu extends Menu{

    public HostMenu(Player player) {
        super(player, "host", "§8Hostear eventos", 3);
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
        if (event.getCurrentItem().getType() == Material.LEASH) {
            if(!getPlayer().hasPermission("practice.host")) {
                return;
            }
            else if(Practice.getInstance().getEventManager().getByName("Sumo").getState() == EventState.UNANNOUNCED) {
                player.performCommand("host sumo");
            }
            else if(Practice.getInstance().getEventManager().getByName("Sumo").getState() != EventState.UNANNOUNCED) {
                return;
            }
            else if(System.currentTimeMillis() < Practice.getInstance().getEventManager().getCooldown()) {
                return;
            }
            player.performCommand("host sumo");
        }
    }

    @Override
    public void update() {
        List<String> sumo = new ArrayList<>();
        if(!getPlayer().hasPermission("practice.host")) {
            sumo.add("");
            sumo.add("§fNo tienes permisos, necesitas un rango §3§lSUPERIOR§f");
            sumo.add("§fcompralo en §3tienda.groyland.net§f.");
            sumo.add("");
        } else if(Practice.getInstance().getEventManager().getByName("Sumo").getState() == EventState.UNANNOUNCED) {
            sumo.add("");
            sumo.add("§f¡Click para hostear un evento Sumo!");
            sumo.add("");
        } else if(Practice.getInstance().getEventManager().getByName("Sumo").getState() != EventState.UNANNOUNCED) {
            sumo.add("");
            sumo.add("§cActualmente ya hay un evento activo.");
            sumo.add("");
        } else if(System.currentTimeMillis() < Practice.getInstance().getEventManager().getCooldown()) {
            sumo.add("");
            sumo.add("§fHay que esperar §3 " + TimeUtil.convertToFormat(Practice.getInstance().getEventManager().getCooldown()) + "§fsegundos");
            sumo.add("§fpara volver a comenzar un evento.");
            sumo.add("");
        }
        sumo.add("§f");
        this.setItem(11, new ItemBuilder2(Material.LEASH).setTitle("§a§lSumo").setLore(sumo));
        this.setItem(15, new ItemBuilder2(Material.BARRIER).setTitle("§c§lEn desarrollo..."));
    }
    
}
