package net.latinplay.practice.events.parkour;

import net.latinplay.practice.events.PracticeEvent;
import java.util.UUID;
import net.latinplay.practice.CustomLocation;
import net.latinplay.practice.events.EventPlayer;

public class ParkourPlayer extends EventPlayer
{
    private ParkourState state;
    private CustomLocation lastCheckpoint;
    private int checkpointId;
    
    public ParkourPlayer(final UUID uuid, final PracticeEvent event) {
        super(uuid, event);
        this.state = ParkourState.WAITING;
    }
    
    public void setState(final ParkourState state) {
        this.state = state;
    }
    
    public void setLastCheckpoint(final CustomLocation lastCheckpoint) {
        this.lastCheckpoint = lastCheckpoint;
    }
    
    public void setCheckpointId(final int checkpointId) {
        this.checkpointId = checkpointId;
    }
    
    public ParkourState getState() {
        return this.state;
    }
    
    public CustomLocation getLastCheckpoint() {
        return this.lastCheckpoint;
    }
    
    public int getCheckpointId() {
        return this.checkpointId;
    }
    
    public enum ParkourState
    {
        WAITING("WAITING", 0), 
        INGAME("INGAME", 1);
        
        private ParkourState(final String s, final int n) {
        }
    }
}
