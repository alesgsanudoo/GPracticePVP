package me.groyteam.practice.managers;

import me.groyteam.practice.Practice;
import me.groyteam.practice.party.Party;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.player.PlayerState;
import me.groyteam.practice.util.TtlHashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.UUID;
import java.util.Map;

public class PartyManager
{
    private final Practice plugin;
    private final Map<UUID, List<UUID>> partyInvites;
    private final Map<UUID, Party> parties;
    private final Map<UUID, UUID> partyLeaders;
    
    public PartyManager() {
        this.plugin = Practice.getInstance();
        this.partyInvites = new TtlHashMap<>(TimeUnit.SECONDS, 15L);
        this.parties = new HashMap<>();
        this.partyLeaders = new HashMap<>();
    }
    
    public boolean isLeader(final UUID uuid) {
        return this.parties.containsKey(uuid);
    }
    
    public void removePartyInvites(final UUID uuid) {
        this.partyInvites.remove(uuid);
    }
    
    public boolean hasPartyInvite(final UUID player, final UUID other) {
        return this.partyInvites.get(player) != null && this.partyInvites.get(player).contains(other);
    }
    
    public void createPartyInvite(final UUID requester, final UUID requested) {
        this.partyInvites.computeIfAbsent(requested, k -> new ArrayList()).add(requester);
    }
    
    public boolean isInParty(final UUID player, final Party party) {
        final Party targetParty = this.getParty(player);
        return targetParty != null && targetParty.getLeader() == party.getLeader();
    }
    
    public Party getParty(final UUID player) {
        if (this.parties.containsKey(player)) {
            return this.parties.get(player);
        }
        if (this.partyLeaders.containsKey(player)) {
            final UUID leader = this.partyLeaders.get(player);
            return this.parties.get(leader);
        }
        return null;
    }
    
    public void createParty(final Player player) {
        final Party party = new Party(player.getUniqueId());
        this.parties.put(player.getUniqueId(), party);
        this.plugin.getInventoryManager().addParty(player);
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        player.sendMessage(ChatColor.YELLOW + "§3§lArenaPvP §8» §aHas creado una party.");
    }
    
    private void disbandParty(final Party party, final boolean tournament) {
        this.plugin.getInventoryManager().removeParty(party);
        this.parties.remove(party.getLeader());
        party.broadcast(ChatColor.YELLOW + "§3§lArenaPvP §8» §cTe has ido de la party, por lo que será eliminada.");
        party.members().forEach(member -> {
            PlayerData memberData = this.plugin.getPlayerManager().getPlayerData(member.getUniqueId());
            if (this.partyLeaders.get(memberData.getUniqueId()) != null) {
                this.partyLeaders.remove(memberData.getUniqueId());
            }
            if (memberData.getPlayerState() == PlayerState.SPAWN) {
                this.plugin.getPlayerManager().sendToSpawnAndReset(member);
            }
        });
    }
    
    public void leaveParty(final Player player) {
        final Party party = this.getParty(player.getUniqueId());
        if (party == null) {
            return;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (this.parties.containsKey(player.getUniqueId())) {
            this.disbandParty(party, false);
        }
        else if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
            this.disbandParty(party, true);
        }
        else {
            party.broadcast("§8[§c-§8] §c" + player.getName() + " §fse ha salido de la Party.");
            party.removeMember(player.getUniqueId());
            this.partyLeaders.remove(player.getUniqueId());
            this.plugin.getInventoryManager().updateParty(party);
        }
        switch (playerData.getPlayerState()) {
            case FIGHTING: {
                this.plugin.getMatchManager().removeFighter(player, playerData, false);
                break;
            }
            case SPECTATING: {
                if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                    this.plugin.getEventManager().removeSpectator(player);
                    break;
                }
                this.plugin.getMatchManager().removeSpectator(player);
                break;
            }
        }
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }
    
    public void joinParty(final UUID leader, final Player player) {
        final Party party = this.getParty(leader);
        if (this.plugin.getTournamentManager().getTournament(leader) != null) {
            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEse jugador está jugando en un torneo.");
            return;
        }
        this.partyLeaders.put(player.getUniqueId(), leader);
        party.addMember(player.getUniqueId());
        this.plugin.getInventoryManager().updateParty(party);
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        party.broadcast("§8[§a+§8] §a" + player.getName() + "§f ha entrado a la Party.");
    }
}