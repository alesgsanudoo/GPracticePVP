package com.bizarrealex.aether.event;

import org.bukkit.entity.Player;
import com.bizarrealex.aether.scoreboard.Board;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class BoardCreateEvent extends Event
{
    private static final HandlerList handlers;
    private final Board board;
    private final Player player;
    
    static {
        handlers = new HandlerList();
    }
    
    public BoardCreateEvent(final Board board, final Player player) {
        this.board = board;
        this.player = player;
    }
    
    @Override
    public HandlerList getHandlers() {
        return BoardCreateEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return BoardCreateEvent.handlers;
    }
    
    public Board getBoard() {
        return this.board;
    }
    
    public Player getPlayer() {
        return this.player;
    }
}
