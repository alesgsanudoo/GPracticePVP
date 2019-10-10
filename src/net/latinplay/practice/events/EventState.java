package net.latinplay.practice.events;

public enum EventState
{
    UNANNOUNCED("UNANNOUNCED", 0), 
    WAITING("WAITING", 1), 
    STARTED("STARTED", 2);
    
    private EventState(final String s, final int n) {
    }
}
