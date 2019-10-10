package me.groyteam.practice.settings.item;

import me.groyteam.practice.util.ItemBuilder;
import me.groyteam.practice.util.inventory.UtilItem;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public enum ProfileOptionsItem
{
    DUEL_REQUESTS("DUEL_REQUESTS", 0, UtilItem.createItem(Material.DIAMOND_SWORD, 1, (short)0, String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + "Duelos"), "§fGestionar si recibir o no §finvitaciones a duelos."),
    PARTY_INVITES("PARTY_INVITES", 1, UtilItem.createItem(Material.NAME_TAG, 1, (short)0, String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + "Invitaciones de Party"), "§fGestionar si recibir o §fno §finvitaciones a una party."),
    TOGGLE_SCOREBOARD("TOGGLE_SCOREBOARD", 2, UtilItem.createItem(Material.EMPTY_MAP, 1, (short)0, String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + "Alternar Scoreboard"), "§fActivar/Desactivar invitaciones."),
    ALLOW_SPECTATORS("ALLOW_SPECTATORS", 3, UtilItem.createItem(Material.COMPASS, 1, (short)0, String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + "Permitir Espectadores"), "§fPermitir/Denegar que los jugadores pueden §fespectear tu combate.");
    
    private ItemStack item;
    private List<String> description;
    
    ProfileOptionsItem(final String s, final int n, final ItemStack item, final String description) {
        this.item = item;
        (this.description = new ArrayList<>()).add("");
        String parts = "";
        for (int i = 0; i < description.split(" ").length; ++i) {
            final String part = description.split(" ")[i];
            parts = parts + part + " ";
            if (i == 4 || i + 1 == description.split(" ").length) {
                this.description.add(ChatColor.GRAY + parts.trim());
                parts = "";
            }
        }
        this.description.add(" ");
    }
    
    public ItemStack getItem(final ProfileOptionsItemState state) {
        if (this == ProfileOptionsItem.DUEL_REQUESTS || this == ProfileOptionsItem.PARTY_INVITES || this == ProfileOptionsItem.ALLOW_SPECTATORS) {
            final List<String> lore = new ArrayList<>(this.description);
            lore.add("  " + ((state == ProfileOptionsItemState.ENABLED) ? (ChatColor.GREEN + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.WHITE + this.getOptionDescription(ProfileOptionsItemState.ENABLED));
            lore.add("  " + ((state == ProfileOptionsItemState.DISABLED) ? (ChatColor.RED + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.WHITE + this.getOptionDescription(ProfileOptionsItemState.DISABLED));
            return new ItemBuilder(this.item).lore(lore).build();
        }

        if (this == ProfileOptionsItem.TOGGLE_SCOREBOARD) {
            final List<String> lore = new ArrayList<>(this.description);
            lore.add("  " + ((state == ProfileOptionsItemState.ENABLED) ? (ChatColor.GREEN + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.WHITE + this.getOptionDescription(ProfileOptionsItemState.ENABLED));
            lore.add("  " + ((state == ProfileOptionsItemState.DISABLED) ? (ChatColor.RED + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.WHITE + this.getOptionDescription(ProfileOptionsItemState.DISABLED));
            return new ItemBuilder(this.item).lore(lore).build();
        }
        return this.getItem(ProfileOptionsItemState.DISABLED);
    }
    
    public String getOptionDescription(final ProfileOptionsItemState state) {
        if (null != this) switch (this) {
            case DUEL_REQUESTS:
            case PARTY_INVITES:
            case ALLOW_SPECTATORS:
                if (state == ProfileOptionsItemState.ENABLED) {
                    return "Enable";
                }   if (state == ProfileOptionsItemState.DISABLED) {
                    return "Disable";
                }   break;
            case TOGGLE_SCOREBOARD:
                if (state == ProfileOptionsItemState.ENABLED) {
                    return "Enable";
                }   if (state == ProfileOptionsItemState.DISABLED) {
                    return "Disable";
                }   break;
            default:
                break;
        }
        return this.getOptionDescription(ProfileOptionsItemState.DISABLED);
    }
    
    public static ProfileOptionsItem fromItem(final ItemStack itemStack) {
        ProfileOptionsItem[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final ProfileOptionsItem item = values[i];
            ProfileOptionsItemState[] values2;
            for (int length2 = (values2 = ProfileOptionsItemState.values()).length, j = 0; j < length2; ++j) {
                final ProfileOptionsItemState state = values2[j];
                if (item.getItem(state).isSimilar(itemStack)) {
                    return item;
                }
            }
        }
        return null;
    }
}
