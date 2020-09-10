package com.siinusmc.bingo;

import org.bukkit.OfflinePlayer;

public final class TeamManager {

    public static BingoTeam getTeam(OfflinePlayer p) {
        for(BingoTeam t : Bingo.TEAMS) {
            for(OfflinePlayer pl : t.players) {
                if(pl.equals(p)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static void calculateRanks() {
        for(BingoTeam t : Bingo.TEAMS) {
            t.rank = 1;
            for(BingoTeam t2 : Bingo.TEAMS) {
                if(t2.equals(t)) {
                    continue;
                }
                if(t2.count>t.count) {
                    t.rank++;
                }
            }
        }
    }
}
