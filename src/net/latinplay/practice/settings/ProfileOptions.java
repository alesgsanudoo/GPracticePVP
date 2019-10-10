package net.latinplay.practice.settings;

import net.latinplay.practice.settings.item.ProfileOptionsItem;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import net.latinplay.practice.settings.item.ProfileOptionsItemState;

public class ProfileOptions
{
    private boolean duelRequests;
    private boolean partyInvites;
    private boolean spectators;
    private ProfileOptionsItemState scoreboard;
    private ProfileOptionsItemState time;
    
    public ProfileOptions() {
        this.duelRequests = true;
        this.partyInvites = true;
        this.spectators = true;
        this.time = ProfileOptionsItemState.DAY;
    }
    
    public Inventory getInventory() {
        final Inventory toReturn = Bukkit.createInventory(null, 36, "Opciones");
        toReturn.setItem(22, ProfileOptionsItem.DUEL_REQUESTS.getItem(this.duelRequests ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
        toReturn.setItem(12, ProfileOptionsItem.PARTY_INVITES.getItem(this.partyInvites ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
        toReturn.setItem(13, ProfileOptionsItem.ALLOW_SPECTATORS.getItem(this.spectators ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
        toReturn.setItem(14, ProfileOptionsItem.TOGGLE_SCOREBOARD.getItem(this.scoreboard));
        return toReturn;
    }
    
    public boolean isDuelRequests() {
        return this.duelRequests;
    }
    
    public ProfileOptions setDuelRequests(final boolean duelRequests) {
        this.duelRequests = duelRequests;
        return this;
    }
    
    public boolean isPartyInvites() {
        return this.partyInvites;
    }
    
    public ProfileOptions setPartyInvites(final boolean partyInvites) {
        this.partyInvites = partyInvites;
        return this;
    }
    
    public boolean isSpectators() {
        return this.spectators;
    }
    
    public ProfileOptions setSpectators(final boolean spectators) {
        this.spectators = spectators;
        return this;
    }
    
    public ProfileOptionsItemState getScoreboard() {
        return this.scoreboard;
    }
    
    public ProfileOptions setScoreboard(final ProfileOptionsItemState scoreboard) {
        this.scoreboard = scoreboard;
        return this;
    }
    
    public ProfileOptionsItemState getTime() {
        return this.time;
    }
    
    public ProfileOptions setTime(final ProfileOptionsItemState time) {
        this.time = time;
        return this;
    }
}
