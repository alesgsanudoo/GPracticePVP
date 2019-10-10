package me.groyteam.practice.runnable;

import java.util.Iterator;

import me.groyteam.practice.Practice;
import org.bukkit.entity.Item;

public class ItemDespawnRunnable implements Runnable
{
    private final Practice plugin;

    public ItemDespawnRunnable() {
        this.plugin = Practice.getInstance();
    }

    @Override
    public void run() {
        final Iterator<Item> it = this.plugin.getFfaManager().getItemTracker().keySet().iterator();
        while (it.hasNext()) {
            final Item item = it.next();
            final long l = this.plugin.getFfaManager().getItemTracker().get(item);
            if (l + 15000L < System.currentTimeMillis()) {
                item.remove();
                it.remove();
            }
        }
    }
}
