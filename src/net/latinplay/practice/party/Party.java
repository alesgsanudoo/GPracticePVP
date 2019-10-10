package net.latinplay.practice.party;

import java.util.Objects;
import org.bukkit.entity.Player;
import java.util.stream.Stream;
import java.util.List;
import java.util.ArrayList;
import net.latinplay.practice.match.MatchTeam;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.latinplay.practice.Practice;

public class Party
{
    private final Practice plugin;
    private final UUID leader;
    private final Set<UUID> members;
    private int limit;
    private boolean open;

    public Party(final UUID leader) {
        this.plugin = Practice.getInstance();
        this.members = new HashSet<>();
        this.limit = 50;
        this.leader = leader;
        this.members.add(leader);
    }

    public void addMember(final UUID uuid) {
        this.members.add(uuid);
    }

    public void removeMember(final UUID uuid) {
        this.members.remove(uuid);
    }

    public void broadcast(final String message) {
        this.members().forEach(member -> member.sendMessage(message));
    }

    public MatchTeam[] split() {
        final List<UUID> teamA = new ArrayList<>();
        final List<UUID> teamB = new ArrayList<>();
        for (final UUID member : this.members) {
            if (teamA.size() == teamB.size()) {
                teamA.add(member);
            }
            else {
                teamB.add(member);
            }
        }
        return new MatchTeam[] { new MatchTeam(teamA.get(0), teamA, 0), new MatchTeam(teamB.get(0), teamB, 1) };
    }

    public Stream<Player> members() {
        return this.members.stream().<Player>map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public Practice getPlugin() {
        return this.plugin;
    }

    public UUID getLeader() {
        return this.leader;
    }

    public Set<UUID> getMembers() {
        return this.members;
    }

    public int getLimit() {
        return this.limit;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setLimit(final int limit) {
        this.limit = limit;
    }

    public void setOpen(final boolean open) {
        this.open = open;
    }
}
