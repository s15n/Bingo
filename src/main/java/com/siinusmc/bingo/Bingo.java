package com.siinusmc.bingo;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Bingo extends JavaPlugin {

    public static final String PREFIX = "§6[Bingo] ";

    static boolean RUNNING = false;
    static boolean AUTOSAVE = false;
    static String SAVE_NAME = null;
    static int COUNT = 1;
    static ItemStack[] ITEMS = {};
    static ScoreboardManager SCOREBOARD = null;

    static List<BingoTeam> TEAMS = new ArrayList<>();
    static List<OfflinePlayer> PLAYERS = new ArrayList<>();

    private static Plugin INSTANCE = null;

    @Override
    public void onEnable() {
        INSTANCE = this;

        PluginManager m = Bukkit.getPluginManager();
        getCommand("bingo").setExecutor(new BingoCommand());
        getCommand("top").setExecutor(new TopCommand());
        m.registerEvents(new GUIListener(), this);
        m.registerEvents(new ItemListener(), this);
        m.registerEvents(new DamageCanceller(), this);

        BingoCommand.clearTags();

        Bukkit.getScheduler().runTaskTimer(this,()->{
            if(!RUNNING) {
                return;
            }
            for(int i=0; i<TEAMS.size(); i++) {
                BingoTeam t = TEAMS.get(i);
                /*if(!p.hasMetadata("bingo_count")) {
                    continue;
                }*/
                for(OfflinePlayer p : t.players) {
                    if(p instanceof Player) {
                        ((Player) p).sendActionBar("§a§lBingo§r§a: §fYour Team: #§b"+(i+1)+"§f, Your score:"+" §r§6"+t.count+"§f/§b"+COUNT);
                    }
                }
            }
        },0L,20L);
    }

    @Override
    public void onDisable() {
        BingoCommand.clearTags();
    }

    public static Plugin getInstance() {
        return INSTANCE;
    }
}
