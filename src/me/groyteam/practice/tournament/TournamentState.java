package me.groyteam.practice.tournament;

public enum TournamentState
{
    WAITING("WAITING", 0), 
    STARTING("STARTING", 1), 
    FIGHTING("FIGHTING", 2);
    
    private TournamentState(final String s, final int n) {
    }
}
