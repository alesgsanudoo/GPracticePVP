package net.latinplay.practice.match;

import java.util.List;
import java.util.UUID;
import net.latinplay.practice.team.KillableTeam;

public class MatchTeam extends KillableTeam
{
    private final int teamID;

    public MatchTeam(final UUID leader, final List<UUID> players, final int teamID) {
        super(leader, players);
        this.teamID = teamID;
    }

    public int getTeamID() {
        return this.teamID;
    }
}
