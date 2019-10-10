package me.groyteam.practice.events.oitc;

import me.groyteam.practice.events.PracticeEvent;
import org.bukkit.ChatColor;
import java.util.Arrays;

import me.groyteam.practice.events.EventCountdownTask;

public class OITCCountdownTask extends EventCountdownTask
{
    public OITCCountdownTask(final PracticeEvent event) {
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
        this.getEvent().sendMessage(ChatColor.RED + "Not enough players. Event has been cancelled");
        this.getEvent().end();
        this.getEvent().getPlugin().getEventManager().setCooldown(0L);
    }
}
