package me.groyteam.practice.util.timer.event;

import me.groyteam.practice.Practice;
import java.util.Objects;
import org.bukkit.entity.Player;
import me.groyteam.practice.util.timer.Timer;
import java.util.UUID;
import java.util.Optional;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class TimerClearEvent extends Event
{
    private static final HandlerList HANDLERS;
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private Optional<Player> player;

    static {
        HANDLERS = new HandlerList();
    }

    public TimerClearEvent(final Timer timer) {
        this.userUUID = Optional.<UUID>empty();
        this.timer = timer;
    }

    public TimerClearEvent(final UUID userUUID, final Timer timer) {
        this.userUUID = Optional.<UUID>of(userUUID);
        this.timer = timer;
    }

    public TimerClearEvent(final Player player, final Timer timer) {
        Objects.<Player>requireNonNull(player);
        this.player = Optional.<Player>of(player);
        this.userUUID = Optional.<UUID>of(player.getUniqueId());
        this.timer = timer;
    }

    public static HandlerList getHandlerList() {
        return TimerClearEvent.HANDLERS;
    }

    public Optional<Player> getPlayer() {
        if (this.player == null) {
            this.player = (this.userUUID.isPresent() ? Optional.<Player>of(Practice.getInstance().getServer().getPlayer((UUID)this.userUUID.get())) : Optional.<Player>empty());
        }
        return this.player;
    }

    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public HandlerList getHandlers() {
        return TimerClearEvent.HANDLERS;
    }
}
