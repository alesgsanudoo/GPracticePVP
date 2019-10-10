package me.groyteam.practice.events.redrover;

import me.groyteam.practice.events.PracticeEvent;
import java.util.UUID;
import org.bukkit.scheduler.BukkitTask;
import me.groyteam.practice.events.EventPlayer;

public class RedroverPlayer extends EventPlayer
{
    private RedroverState state;
    private RedroverPlayer fightPlayer;
    private BukkitTask fightTask;
    
    public RedroverPlayer(final UUID uuid, final PracticeEvent event) {
        super(uuid, event);
        this.state = RedroverState.WAITING;
    }
    
    public void setState(final RedroverState state) {
        this.state = state;
    }
    
    public void setFightPlayer(final RedroverPlayer fightPlayer) {
        this.fightPlayer = fightPlayer;
    }
    
    public void setFightTask(final BukkitTask fightTask) {
        this.fightTask = fightTask;
    }
    
    public RedroverState getState() {
        return this.state;
    }
    
    public RedroverPlayer getFightPlayer() {
        return this.fightPlayer;
    }
    
    public BukkitTask getFightTask() {
        return this.fightTask;
    }
    
    public enum RedroverState
    {
        WAITING("WAITING", 0), 
        PREPARING("PREPARING", 1), 
        FIGHTING("FIGHTING", 2);
        
        private RedroverState(final String s, final int n) {
        }
    }
}
