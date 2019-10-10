package net.latinplay.practice.match;

public enum MatchState
{
    STARTING("STARTING", 0),
    FIGHTING("FIGHTING", 1),
    SWITCHING("SWITCHING", 2),
    ENDING("ENDING", 3);

    private MatchState(final String s, final int n) {
    }
}
