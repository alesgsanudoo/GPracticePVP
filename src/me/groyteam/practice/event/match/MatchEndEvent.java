package me.groyteam.practice.event.match;

import me.groyteam.practice.match.MatchTeam;
import me.groyteam.practice.match.Match;

public class MatchEndEvent extends MatchEvent
{
    private final MatchTeam winningTeam;
    private final MatchTeam losingTeam;

    public MatchEndEvent(final Match match, final MatchTeam winningTeam, final MatchTeam losingTeam) {
        super(match);
        this.winningTeam = winningTeam;
        this.losingTeam = losingTeam;
    }

    public MatchTeam getWinningTeam() {
        return this.winningTeam;
    }

    public MatchTeam getLosingTeam() {
        return this.losingTeam;
    }
}
