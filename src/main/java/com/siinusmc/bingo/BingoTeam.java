package com.siinusmc.bingo;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BingoTeam {
    List<OfflinePlayer> players;
    int rank = 0;
    int count = 0;
    List<Integer> items = new ArrayList<>();
    int index = 0;
    Scoreboard scoreboard;

    public BingoTeam(OfflinePlayer... players) {
        this.players = new ArrayList<>(Arrays.asList(players));
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    void calculateIndex() {
        if(!Bingo.TEAMS.contains(this)) {
            return;
        }
        for(int i=0; i<Bingo.TEAMS.size(); i++) {
            if(Bingo.TEAMS.get(i).equals(this)) {
                index = i;
            }
        }
    }
}
