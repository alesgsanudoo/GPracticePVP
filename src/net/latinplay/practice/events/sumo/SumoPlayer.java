package net.latinplay.practice.events.sumo;

import net.latinplay.practice.events.PracticeEvent;
import java.util.UUID;
import org.bukkit.scheduler.BukkitTask;
import net.latinplay.practice.events.EventPlayer;
import org.bukkit.entity.Player;

public class SumoPlayer extends EventPlayer
{
    private SumoState state;
    private BukkitTask fightTask;
    private SumoPlayer fighting;
    private Player p;

    public SumoPlayer(Player player, UUID uuid, final PracticeEvent event) {
        super(uuid, event);
        this.state = SumoState.WAITING;
        this.p = player;
    }

    public Player getPlayer() {
        return this.p;
    }

    public void setState(final SumoState state) {
        this.state = state;
    }

    public void setFightTask(final BukkitTask fightTask) {
        this.fightTask = fightTask;
    }

    public void setFighting(final SumoPlayer fighting) {
        this.fighting = fighting;
    }

    public SumoState getState() {
        return this.state;
    }

    public BukkitTask getFightTask() {
        return this.fightTask;
    }

    public SumoPlayer getFighting() {
        return this.fighting;
    }

    public enum SumoState
    {
        WAITING("WAITING", 0),
        PREPARING("PREPARING", 1),
        FIGHTING("FIGHTING", 2),
        ELIMINATED("ELIMINATED", 3);

        private SumoState(final String s, final int n) {
        }
    }
}
