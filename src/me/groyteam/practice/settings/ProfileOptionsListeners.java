package me.groyteam.practice.settings;

import me.groyteam.practice.Practice;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.settings.item.ProfileOptionsItem;
import me.groyteam.practice.settings.item.ProfileOptionsItemState;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;

public class ProfileOptionsListeners implements Listener
{
    @EventHandler
    public void onInventoryInteractEvent(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        final PlayerData profile = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        final Inventory inventory = event.getInventory();
        final ItemStack itemStack = event.getCurrentItem();
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            final Inventory options = profile.getOptions().getInventory();
            if (inventory.getTitle().equals(options.getTitle()) && Arrays.equals(inventory.getContents(), options.getContents())) {
                event.setCancelled(true);
                final ProfileOptionsItem item = ProfileOptionsItem.fromItem(itemStack);
                if (item != null) {
                    if (item == ProfileOptionsItem.DUEL_REQUESTS) {
                        profile.getOptions().setDuelRequests(!profile.getOptions().isDuelRequests());
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().isDuelRequests() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
                    }
                    else if (item == ProfileOptionsItem.PARTY_INVITES) {
                        profile.getOptions().setPartyInvites(!profile.getOptions().isPartyInvites());
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().isPartyInvites() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
                    }
                    else if (item == ProfileOptionsItem.TOGGLE_SCOREBOARD) {
                        if (profile.getOptions().getScoreboard() == ProfileOptionsItemState.DISABLED) {
                            profile.getOptions().setScoreboard(ProfileOptionsItemState.ENABLED);
                        }
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().getScoreboard()));
                    }
                    else if (item == ProfileOptionsItem.ALLOW_SPECTATORS) {
                        profile.getOptions().setSpectators(!profile.getOptions().isSpectators());
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().isSpectators() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
                    }
                }
            }
        }
    }
}
