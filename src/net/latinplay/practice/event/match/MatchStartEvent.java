package net.latinplay.practice.event.match;

import net.latinplay.practice.match.Match;

public class MatchStartEvent extends MatchEvent
{
    public MatchStartEvent(final Match match) {
        super(match);
    }
}
