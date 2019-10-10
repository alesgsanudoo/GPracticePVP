package me.groyteam.practice.event.match;

import me.groyteam.practice.match.Match;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class MatchEvent extends Event
{
    private static final HandlerList HANDLERS;
    private final Match match;

    static {
        HANDLERS = new HandlerList();
    }

    public static HandlerList getHandlerList() {
        return MatchEvent.HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return MatchEvent.HANDLERS;
    }

    public Match getMatch() {
        return this.match;
    }

    public MatchEvent(final Match match) {
        this.match = match;
    }
}
