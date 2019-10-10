package me.groyteam.practice.player;

public enum PlayerState
{
    LOADING("LOADING", 0), 
    SPAWN("SPAWN", 1), 
    EDITING("EDITING", 2), 
    SPECTATING("SPECTATING", 3), 
    QUEUE("QUEUE", 4), 
    FIGHTING("FIGHTING", 5), 
    FFA("FFA", 6), 
    EVENT("EVENT", 7);
    
    private PlayerState(final String s, final int n) {
    }
}
