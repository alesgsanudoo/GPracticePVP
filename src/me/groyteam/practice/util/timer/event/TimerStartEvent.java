package me.groyteam.practice.util.timer.event;

import javax.annotation.Nullable;
import me.groyteam.practice.util.timer.Timer;
import java.util.UUID;
import org.bukkit.entity.Player;
import java.util.Optional;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class TimerStartEvent extends Event
{
    private static final HandlerList HANDLERS;
    private final Optional<Player> player;
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private final long duration;

    static {
        HANDLERS = new HandlerList();
    }

    public TimerStartEvent(final Timer timer, final long duration) {
        this.player = Optional.<Player>empty();
        this.userUUID = Optional.<UUID>empty();
        this.timer = timer;
        this.duration = duration;
    }

    public TimerStartEvent(@Nullable final Player player, final UUID uniqueId, final Timer timer, final long duration) {
        this.player = Optional.<Player>ofNullable(player);
        this.userUUID = Optional.<UUID>ofNullable(uniqueId);
        this.timer = timer;
        this.duration = duration;
    }

    public static HandlerList getHandlerList() {
        return TimerStartEvent.HANDLERS;
    }

    public Optional<Player> getPlayer() {
        return this.player;
    }

    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public long getDuration() {
        return this.duration;
    }

    public HandlerList getHandlers() {
        return TimerStartEvent.HANDLERS;
    }
}
