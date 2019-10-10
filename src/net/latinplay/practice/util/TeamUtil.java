package net.latinplay.practice.util;

import org.bukkit.entity.Player;
import net.latinplay.practice.tournament.TournamentTeam;
import net.latinplay.practice.Practice;
import java.util.UUID;
import net.latinplay.practice.team.KillableTeam;

public class TeamUtil
{
    public static String getNames(final KillableTeam team) {
        String names = "";
        for (int i = 0; i < team.getPlayers().size(); ++i) {
            final UUID teammateUUID = team.getPlayers().get(i);
            final Player teammate = Practice.getInstance().getServer().getPlayer(teammateUUID);
            String name = "";
            if (teammate == null) {
                if (team instanceof TournamentTeam) {
                    name = ((TournamentTeam)team).getPlayerName(teammateUUID);
                }
            }
            else {
                name = teammate.getName();
            }
            final int players = team.getPlayers().size();
            if (teammate != null) {
                names = String.valueOf(names) + name + ((players - 1 == i) ? "" : ((players - 2 == i) ? (String.valueOf((players > 2) ? "," : "") + " & ") : ", "));
            }
        }
        return names;
    }
}
