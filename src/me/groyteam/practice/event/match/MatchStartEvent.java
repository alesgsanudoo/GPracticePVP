package me.groyteam.practice.event.match;

import me.groyteam.practice.match.Match;

public class MatchStartEvent extends MatchEvent
{
    public MatchStartEvent(final Match match) {
        super(match);
    }
}
