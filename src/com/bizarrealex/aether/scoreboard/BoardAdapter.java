package com.bizarrealex.aether.scoreboard;

import java.util.List;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import java.util.Set;
import org.bukkit.entity.Player;

public interface BoardAdapter
{
    String getTitle(final Player p0);
    
    List<String> getScoreboard(final Player p0, final Board p1, final Set<BoardCooldown> p2);
}
