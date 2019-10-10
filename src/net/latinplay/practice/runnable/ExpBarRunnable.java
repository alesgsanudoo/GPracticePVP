package net.latinplay.practice.runnable;

import net.latinplay.practice.events.PracticeEvent;
import java.util.Iterator;
import net.latinplay.practice.events.parkour.ParkourPlayer;
import net.latinplay.practice.events.parkour.ParkourEvent;
import net.latinplay.practice.events.oitc.OITCPlayer;
import net.latinplay.practice.events.oitc.OITCEvent;
import org.bukkit.entity.Player;
import java.util.UUID;
import net.latinplay.practice.util.timer.impl.EnderpearlTimer;
import net.latinplay.practice.Practice;

public class ExpBarRunnable implements Runnable
{
    private final Practice plugin;

    @Override
    public void run() {
        final EnderpearlTimer timer = Practice.getInstance().getTimerManager().<EnderpearlTimer>getTimer(EnderpearlTimer.class);
        for (final UUID uuid : timer.getCooldowns().keySet()) {
            final Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null) {
                final long time = timer.getRemaining(player);
                final int seconds = (int)Math.round(time / 1000.0);
                player.setLevel(seconds);
                player.setExp(time / 15000.0f);
            }
        }
        for (final Player player2 : this.plugin.getServer().getOnlinePlayers()) {
            final PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player2);
            if (event != null && event instanceof OITCEvent) {
                final OITCEvent oitcEvent = (OITCEvent)event;
                final OITCPlayer oitcPlayer = oitcEvent.getPlayer(player2.getUniqueId());
                if (oitcPlayer == null || oitcPlayer.getState() == OITCPlayer.OITCState.WAITING || oitcEvent.getGameTask() == null) {
                    continue;
                }
                final int seconds = oitcEvent.getGameTask().getTime();
                if (seconds < 0) {
                    continue;
                }
                player2.setLevel(seconds);
            }
            else {
                if (event == null || !(event instanceof ParkourEvent)) {
                    continue;
                }
                final ParkourEvent parkourEvent = (ParkourEvent)event;
                final ParkourPlayer parkourPlayer = parkourEvent.getPlayer(player2.getUniqueId());
                if (parkourPlayer == null || parkourPlayer.getState() == ParkourPlayer.ParkourState.WAITING || parkourEvent.getGameTask() == null) {
                    continue;
                }
                final int seconds = parkourEvent.getGameTask().getTime();
                if (seconds < 0) {
                    continue;
                }
                player2.setLevel(seconds);
            }
        }
    }

    public ExpBarRunnable() {
        this.plugin = Practice.getInstance();
    }
}
