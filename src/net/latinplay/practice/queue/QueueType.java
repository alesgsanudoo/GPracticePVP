package net.latinplay.practice.queue;

public enum QueueType
{
    UNRANKED("UNRANKED", 0, "Unranked"),
    RANKED("RANKED", 1, "Ranked");

    private final String name;

    public boolean isRanked() {
        return this != QueueType.UNRANKED;
    }

    public boolean isUnranked() {
        return this != QueueType.RANKED;
    }

    public String getName() {
        return this.name;
    }

    private QueueType(final String s, final int n, final String name) {
        this.name = name;
    }
}
