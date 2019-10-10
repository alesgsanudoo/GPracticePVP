package me.groyteam.practice.settings.item;

public enum ProfileOptionsItemState
{
    ENABLED("ENABLED", 0), 
    DISABLED("DISABLED", 1),
    DAY("DAY", 2),
    SUNSET("SUNSET", 3),
    NIGHT("NIGHT", 4);
    
    private ProfileOptionsItemState(final String s, final int n) {
    }
}
