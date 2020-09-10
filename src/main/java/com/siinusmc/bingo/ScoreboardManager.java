package com.siinusmc.bingo;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;

public final class ScoreboardManager {
    Scoreboard scoreboard;

    public ScoreboardManager() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public void show(Player p) {
        p.setScoreboard(scoreboard);
    }

    public static void update() {
        TeamManager.calculateRanks();
        for (int i=0; i<Bingo.TEAMS.size(); i++) {
            BingoTeam t = Bingo.TEAMS.get(i);
            /*if (!((Player) p).hasMetadata("bingo_count")) {
                continue;
            }*/
            if (t.scoreboard.getObjective("bingo") != null) {
                Objects.requireNonNull(t.scoreboard.getObjective("bingo")).unregister();
            }
            Objective objective = t.scoreboard.registerNewObjective("bingo", "dummy", "§a§lBingo §r§aby Siinus");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            objective.getScore(" ").setScore(15);
            StringBuilder first = new StringBuilder();
            for(int j=0; j<Bingo.TEAMS.size(); j++) {
                if(Bingo.TEAMS.get(j).rank==1) {
                    if(first.indexOf("#")>-1) {
                        first.append("§f, ");
                    }
                    first.append("§f#§b");
                    first.append(j+1);
                }
            }
            objective.getScore("§fFirst place: "+first.toString()).setScore(14);
            objective.getScore("§fYour rank: §b" + t.rank).setScore(13);
            objective.getScore("  ").setScore(12);
            objective.getScore("§fItems (§a" + (Bingo.COUNT - t.count) + " §fto go):").setScore(11);
            int c = 10;
            for (int j = 0; j < Bingo.COUNT; j++) {
                ItemStack item = Bingo.ITEMS[j];
                if (t.items.contains(j)) {
                    continue;
                }
                if (c <= 1) {
                    objective.getScore("§8...and §7" + (Bingo.COUNT - t.count - 10) + " §8more.").setScore(1);
                    break;
                }
                objective.getScore("§7- " + item.getI18NDisplayName()).setScore(c--);
            }

            for(OfflinePlayer p : t.players) {
                if(p instanceof Player) {
                    ((Player) p).setScoreboard(t.scoreboard);
                }
            }
        }
    }

    public static void hide() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }
}
