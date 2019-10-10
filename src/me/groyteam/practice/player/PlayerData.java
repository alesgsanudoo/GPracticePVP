package me.groyteam.practice.player;

import java.util.HashMap;
import me.groyteam.practice.settings.ProfileOptions;
import java.util.UUID;
import me.groyteam.practice.kit.PlayerKit;
import java.util.Map;
import me.groyteam.practice.Practice;
import me.groyteam.practice.kit.Kit;
import org.bukkit.entity.Player;

public class PlayerData
{
    public static final int DEFAULT_ELO = 800;
    private final Map<String, Map<Integer, PlayerKit>> playerKits;
    private final Map<String, Integer> rankedLosses;
    private final Map<String, Integer> rankedWins;
    private final Map<String, Integer> rankedElo;
    private final UUID uniqueId;
    private PlayerState playerState;
    private final Player p;
    private UUID currentMatchID;
    private UUID duelSelecting;
    private ProfileOptions options;
    private int eloRange;
    private int pingRange;
    private int teamID;
    private int rematchID;
    private int missedPots;
    private int longestCombo;
    private int combo;
    private int hits;
    private int rankeds;
    private int rankedWin;
    private int rankedLoss;
    private int rankedElos;
    private int oitcEventKills;
    private int oitcEventDeaths;
    private int oitcEventWins;
    private int oitcEventLosses;
    private int sumoEventWins;
    private int sumoEventLosses;
    private int parkourEventWins;
    private int parkourEventLosses;
    private int redroverEventWins;
    private int redroverEventLosses;
    
    public int getWins(final String kitName) {
        return this.rankedWins.computeIfAbsent(kitName, k -> 0);
    }
    
    public void setWins(final String kitName, final int wins) {
        this.rankedWins.put(kitName, wins);
    }
    
    public int getRankeds() {
        return this.rankeds;
    }
    
    public int setRankeds(int i) {
        return this.rankeds = i;
    }
    
    public Player getPlayer() {
        return this.p;
    }
    
    public int rankRanked() {
        if(p.hasPermission("practice.phoenix")) {
            this.setRankeds(40);
            return 40;
        }
        if(p.hasPermission("practice.staff")) {
            this.setRankeds(10000000);
            return 10000000;
        }
        if(p.hasPermission("practice.gnome")) {
            this.setRankeds(10000000);
            return 10000000;
        }
        if(p.hasPermission("practice.smurf")) {
            this.setRankeds(80);
            return 80;
        }
        this.setRankeds(20);
        return 20;
    }
    
    public String getRankFromElo() {
        if(this.getGlobalRankedElo() >= 0 && this.getGlobalRankedElo() <= 899) {
            return "§4Cobre §8[§fIV§8]";
        }
        if(this.getGlobalRankedElo() >= 900 && this.getGlobalRankedElo() <= 999) {
            return "&4Cobre §8[§fIII§8]";
        }
        if(this.getGlobalRankedElo() >= 1000 && this.getGlobalRankedElo() <= 1199) {
            return "§4Cobre §8[§fII§8]";
        }
        if(this.getGlobalRankedElo() >= 1200 && this.getGlobalRankedElo() <= 1399) {
            return "§4Cobre §8[§fI§8]";
        }
        if(this.getGlobalRankedElo() >= 1400 && this.getGlobalRankedElo() <= 1599) {
            return "§cBronce §8[§fIV§8]";
        }
        if(this.getGlobalRankedElo() >= 1600 && this.getGlobalRankedElo() <= 1899) {
            return "§cBronce §8[§fIII§8]";
        }
        if(this.getGlobalRankedElo() >= 1900 && this.getGlobalRankedElo() <= 2099) {
            return "§cBronce §8[§fII§8]";
        }
        if(this.getGlobalRankedElo() >= 2100 && this.getGlobalRankedElo() <= 2299) {
            return "§cBronce §8[§fI§8]";
        }
        if(this.getGlobalRankedElo() >= 2300 && this.getGlobalRankedElo() <= 2499) {
            return "§7Plata §8[§fIV§8]";
        }
        if(this.getGlobalRankedElo() >= 2500 && this.getGlobalRankedElo() <= 2699) {
            return "§7Plata §8[§fIII§8]";
        }
        if(this.getGlobalRankedElo() >= 2700 && this.getGlobalRankedElo() <= 2799) {
            return "§7Plata §8[§fII§8]";
        }
        if(this.getGlobalRankedElo() >= 2800 && this.getGlobalRankedElo() <= 2899) {
            return "§7Plata §8[§fI§8]";
        }
        if(this.getGlobalRankedElo() >= 2900 && this.getGlobalRankedElo() <= 3099) {
            return "§6Oro §8[§fIV§8]";
        }
        if(this.getGlobalRankedElo() >= 3100 && this.getGlobalRankedElo() <= 3299) {
            return "§6Oro §8[§fIII§8]";
        }
        if(this.getGlobalRankedElo() >= 3300 && this.getGlobalRankedElo() <= 3999) {
            return "§6Oro §8[§fII§8]";
        }
        if(this.getGlobalRankedElo() >= 3400 && this.getGlobalRankedElo() <= 3499) {
            return "§6Oro §8[§fI§8]";
        }
        if(this.getGlobalRankedElo() >= 3500 && this.getGlobalRankedElo() <= 3699) {
            return "§bPlatino §8[§fIII§8]";
        }
        if(this.getGlobalRankedElo() >= 3700 && this.getGlobalRankedElo() <= 3799) {
            return "§bPlatino §8[§fII§8]";
        }
        if(this.getGlobalRankedElo() >= 3800 && this.getGlobalRankedElo() <= 3999) {
            return "§bPlatino §8[§fI§8]";
        }
        if(this.getGlobalRankedElo() >= 4000) {
            return "§5Diamante §8[§fI§8]";
        }
        return "";
    }
    
    public void resetRankeds() {
        if(p.hasPermission("latinpractice.king")) {
            this.setRankeds(Practice.getInstance().getConfig().getInt("Rankeds.king.Cantidad"));
            return;
        }
        if(p.hasPermission("latinpractice.staff")) {
            this.setRankeds(Practice.getInstance().getConfig().getInt("Rankeds.Staff.Cantidad"));
            return;
        }
        if(p.hasPermission("latinpractice.dragon")) {
            this.setRankeds(Practice.getInstance().getConfig().getInt("Rankeds.dragon.Cantidad"));
            return;
        }
        if(p.hasPermission("latinpractice.spirit")) {
            this.setRankeds(Practice.getInstance().getConfig().getInt("Rankeds.spirit.Cantidad"));
            return;
        }
        if(p.hasPermission("latinpractice.elf")) {
            this.setRankeds(Practice.getInstance().getConfig().getInt("Rankeds.elf.Cantidad"));
            return;
        }
        if(p.hasPermission("latinpractice.vip")) {
            this.setRankeds(Practice.getInstance().getConfig().getInt("Rankeds.vip.Cantidad"));
            return;
        }
        this.setRankeds(Practice.getInstance().getConfig().getInt("Rankeds.Default.Cantidad"));
    }
    
    public int getLosses(final String kitName) {
        return this.rankedLosses.computeIfAbsent(kitName, k -> 0);
    }
    
    public void setLosses(final String kitName, final int losses) {
        this.rankedLosses.put(kitName, losses);
    }
    
    public int getElo(final String kitName) {
        return this.rankedElo.computeIfAbsent(kitName, k -> 800);
    }
    
    public void setElo(final String kitName, final int elo) {
        this.rankedElo.put(kitName, elo);
    }
    
    public void addPlayerKit(final int index, final PlayerKit playerKit) {
        this.getPlayerKits(playerKit.getName()).put(index, playerKit);
    }
    
    public Map<Integer, PlayerKit> getPlayerKits(final String kitName) {
        return this.playerKits.computeIfAbsent(kitName, k -> new HashMap());
    }
    
    public int getGlobalRankedElo() {
        int elo = 0;
        elo = elo + this.getElo("BuildUHC");
        elo = elo + this.getElo("Gapple");
        elo = elo + this.getElo("NoDebuff");
        elo = elo + this.getElo("Debuff");
        elo = elo + this.getElo("Archer");
        elo = elo + this.getElo("Sumo");
        elo = elo + this.getElo("Soup");
        elo = elo + this.getElo("Classic");
        elo = elo + this.getElo("Combo");
        elo = elo / 9;
        this.rankedElos = elo;
        return this.rankedElos;
    }
    
    public int getRankedLosses() {
        int loss = 0;
        for(Kit kit : Practice.getInstance().getKitManager().getKits()) {
            loss = loss + this.getLosses(kit.getName());
        }
        this.rankedLoss = loss;
        return this.rankedLoss;
    }
    
    public int getRankedWins() {
        int wins = 0;
        for(Kit kit : Practice.getInstance().getKitManager().getKits()) {
            wins = wins + this.getWins(kit.getName());
        }
        this.rankedWin = wins;
        return this.rankedWin;
    }
    
    /*public int getGlobalStats(final String type) {
        int i = 0;
        int count = 0;
        for (final Kit kit : Practice.getInstance().getKitManager().getKits()) {
            final String upperCase;
            switch (upperCase = type.toUpperCase()) {
                case "LOSSES": {
                    i += this.getLosses(kit.getName());
                    break;
                }
                case "ELO": {
                    i += this.getElo(kit.getName());
                    break;
                }
                case "WINS": {
                    i += this.getWins(kit.getName());
                    break;
                }
                default:
                    break;
            }
            ++count;
        }
        if (i == 0) {
            i = 1;
        }
        if (count == 0) {
            count = 1;
        }
        return type.toUpperCase().equalsIgnoreCase("ELO") ? Math.round(i / count) : i;
    }*/
    
    public UUID getUniqueId() {
        return this.uniqueId;
    }
    
    public PlayerState getPlayerState() {
        return this.playerState;
    }
    
    public UUID getCurrentMatchID() {
        return this.currentMatchID;
    }
    
    public UUID getDuelSelecting() {
        return this.duelSelecting;
    }
    
    public ProfileOptions getOptions() {
        return this.options;
    }
    
    public int getEloRange() {
        return this.eloRange;
    }
    
    public int getPingRange() {
        return this.pingRange;
    }
    
    public int getTeamID() {
        return this.teamID;
    }
    
    public int getRematchID() {
        return this.rematchID;
    }
    
    public int getMissedPots() {
        return this.missedPots;
    }
    
    public int getLongestCombo() {
        return this.longestCombo;
    }
    
    public int getCombo() {
        return this.combo;
    }
    
    public int getHits() {
        return this.hits;
    }
    
    public int getOitcEventKills() {
        return this.oitcEventKills;
    }
    
    public int getOitcEventDeaths() {
        return this.oitcEventDeaths;
    }
    
    public int getOitcEventWins() {
        return this.oitcEventWins;
    }
    
    public int getOitcEventLosses() {
        return this.oitcEventLosses;
    }
    
    public int getSumoEventWins() {
        return this.sumoEventWins;
    }
    
    public int getSumoEventLosses() {
        return this.sumoEventLosses;
    }
    
    public int getParkourEventWins() {
        return this.parkourEventWins;
    }
    
    public int getParkourEventLosses() {
        return this.parkourEventLosses;
    }
    
    public int getRedroverEventWins() {
        return this.redroverEventWins;
    }
    
    public int getRedroverEventLosses() {
        return this.redroverEventLosses;
    }
    
    public void setPlayerState(final PlayerState playerState) {
        this.playerState = playerState;
    }
    
    public void setCurrentMatchID(final UUID currentMatchID) {
        this.currentMatchID = currentMatchID;
    }
    
    public void setDuelSelecting(final UUID duelSelecting) {
        this.duelSelecting = duelSelecting;
    }
    
    public void setOptions(final ProfileOptions options) {
        this.options = options;
    }
    
    public void setEloRange(final int eloRange) {
        this.eloRange = eloRange;
    }
    
    public void setPingRange(final int pingRange) {
        this.pingRange = pingRange;
    }
    
    public void setTeamID(final int teamID) {
        this.teamID = teamID;
    }
    
    public void setRematchID(final int rematchID) {
        this.rematchID = rematchID;
    }
    
    public void setMissedPots(final int missedPots) {
        this.missedPots = missedPots;
    }
    
    public void setLongestCombo(final int longestCombo) {
        this.longestCombo = longestCombo;
    }
    
    public void setCombo(final int combo) {
        this.combo = combo;
    }
    
    public void setHits(final int hits) {
        this.hits = hits;
    }
    
    public void setOitcEventKills(final int oitcEventKills) {
        this.oitcEventKills = oitcEventKills;
    }
    
    public void setOitcEventDeaths(final int oitcEventDeaths) {
        this.oitcEventDeaths = oitcEventDeaths;
    }
    
    public void setOitcEventWins(final int oitcEventWins) {
        this.oitcEventWins = oitcEventWins;
    }
    
    public void setOitcEventLosses(final int oitcEventLosses) {
        this.oitcEventLosses = oitcEventLosses;
    }
    
    public void setSumoEventWins(final int sumoEventWins) {
        this.sumoEventWins = sumoEventWins;
    }
    
    public void setSumoEventLosses(final int sumoEventLosses) {
        this.sumoEventLosses = sumoEventLosses;
    }
    
    public void setParkourEventWins(final int parkourEventWins) {
        this.parkourEventWins = parkourEventWins;
    }
    
    public void setParkourEventLosses(final int parkourEventLosses) {
        this.parkourEventLosses = parkourEventLosses;
    }
    
    public void setRedroverEventWins(final int redroverEventWins) {
        this.redroverEventWins = redroverEventWins;
    }
    
    public void setRedroverEventLosses(final int redroverEventLosses) {
        this.redroverEventLosses = redroverEventLosses;
    }
    
    public PlayerData(Player p, UUID uniqueId) {
        this.playerKits = new HashMap<>();
        this.rankedLosses = new HashMap<>();
        this.rankedWins = new HashMap<>();
        this.rankedElo = new HashMap<>();
        this.playerState = PlayerState.LOADING;
        this.options = new ProfileOptions();
        this.eloRange = 250;
        this.pingRange = 50;
        this.teamID = -1;
        this.rematchID = -1;
        this.rankedElos = 0;
        this.rankedWin = 0;
        this.rankedLoss = 0;
        this.rankeds = 20;
        this.p = p;
        this.uniqueId = uniqueId;
    }
}
