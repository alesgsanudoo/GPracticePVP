package net.latinplay.practice.events.sumo;

import org.bukkit.ChatColor;
import java.util.Arrays;
import net.latinplay.practice.events.PracticeEvent;
import net.latinplay.practice.events.EventCountdownTask;

public class SumoCountdownTask extends EventCountdownTask
{
    public SumoCountdownTask(final PracticeEvent event) {
        super(event, 60);
    }

    @Override
    public boolean shouldAnnounce(final int timeUntilStart) {
        return Arrays.<Integer>asList(45, 30, 15, 10, 5).contains(timeUntilStart);
    }

    @Override
    public boolean canStart() {
        return this.getEvent().getPlayers().size() >= 2;
    }

    @Override
    public void onCancel() {
        this.getEvent().sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo hay sufientes jugadores para empezar, el evento será cancelado.");
        this.getEvent().end();
        this.getEvent().getPlugin().getEventManager().setCooldown(0L);
    }
}
