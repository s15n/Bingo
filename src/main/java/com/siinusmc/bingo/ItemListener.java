package com.siinusmc.bingo;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ItemListener implements Listener {

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if(e.getEntity() instanceof Player && Bingo.PLAYERS.contains((OfflinePlayer)e.getEntity())) {
            check((Player) e.getEntity(), e.getItem().getItemStack());
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if(Bingo.PLAYERS.contains((OfflinePlayer)e.getPlayer())) {
            check(e.getPlayer(), new ItemStack(e.getBlock().getType()));
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        if(Bingo.PLAYERS.contains((OfflinePlayer)e.getPlayer())) {
            check(e.getPlayer(), e.getItem());
        }
    }

    public static void check(Player p, ItemStack item) {
        if(!Bingo.RUNNING) {
            return;
        }
        for(int i=0; i<Bingo.ITEMS.length; i++) {
            ItemStack req = Bingo.ITEMS[i];
            if(req.getType()!=item.getType()) {
                continue;
            }
            if(req.getEnchantments().size()==0) {
                BingoTeam t = TeamManager.getTeam(p);
                if(t == null) {
                    continue;
                }
                checkItem(p, t, item, i);
                continue;
            }
            if(req.getEnchantments().equals(item.getEnchantments())) {
                BingoTeam t = TeamManager.getTeam(p);
                if(t == null) {
                    continue;
                }
                checkItem(p, t, item, i);
            }
        }
        if(Bingo.AUTOSAVE) {
            BingoCommand.saveData(p,"./plugins/bingo/","./plugins/bingo/"+(Bingo.SAVE_NAME==null?System.currentTimeMillis():Bingo.SAVE_NAME)+".json",false);
        }
        ScoreboardManager.update();
    }

    private static void checkItem(Player p, BingoTeam t, ItemStack item, int i) {
        if(!t.items.contains(i)) {
            t.items.add(i);
            //int found = p.hasMetadata("bingo_count")?p.getMetadata("bingo_count").get(0).asInt()+1:1;
            //p.setMetadata("bingo_count",new FixedMetadataValue(Bingo.getInstance(),found));
            t.count++;
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.VOICE, 1, 1);
            for(OfflinePlayer op : Bingo.PLAYERS) {
                if(op instanceof Player) {
                    t.calculateIndex();
                    ((Player)op).sendTitle("§a" + item.getI18NDisplayName(), "§fWurde von §b" + p.getName() + "§f(Team #§b"+(t.index+1)+"§f) gefunden!", 0, 30, 10);
                    ((Player)op).sendMessage(Bingo.PREFIX + "§b" + p.getName() + "§f(Team #§b"+(t.index+1)+"§f) hat §a" + item.getI18NDisplayName() + " §fgefunden (" + t.count + "/" + Bingo.COUNT + ")!");
                }
            }
            if(t.count>=Bingo.COUNT) {
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.VOICE, 1, 1);
                for(OfflinePlayer op : Bingo.PLAYERS) {
                    if(op instanceof Player) {
                        if(t.players.size()==1) {
                            ((Player) op).sendTitle("§f§n" + p.getName(), "§bhat §a§lBingo §bgewonnen!", 0, 60, 20);
                            ((Player) op).sendMessage(Bingo.PREFIX + "§b" + p.getName() + " §fhat §a§lBingo §fgewonnwn! Herzlichen Glückwunsch!");
                        } else {
                            t.calculateIndex();
                            ((Player) op).sendTitle("§f§nTeam #§b§n" + (t.index+1), "§bhat §a§lBingo §bgewonnen!", 0, 60, 20);
                            ((Player) op).sendMessage(Bingo.PREFIX + "§bTeam §f#§b" + (t.index+1) + " §fhat §a§lBingo §fgewonnwn! Herzlichen Glückwunsch!");
                            ((Player) op).sendMessage("§eMitglieder:");
                            for(OfflinePlayer tp : t.players) {
                                if(tp instanceof Player) {
                                    ((Player) op).sendMessage("§7 - §f"+tp.getName());
                                }
                            }
                        }
                    }
                }
                BingoCommand.stopGame();
            }
        }
    }

    public static void check(Player p, Inventory inv) {
        for(ItemStack item : inv) {
            if(item!=null) {
                check(p, item);
            }
        }
    }
}
