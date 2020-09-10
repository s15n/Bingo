package com.siinusmc.bingo;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public final class DamageCanceller implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getCause()==EntityDamageEvent.DamageCause.VOID) {
            return;
        }
        if(Bingo.RUNNING) {
            if(e.getEntity() instanceof Player && Bingo.PLAYERS.contains((OfflinePlayer)e.getEntity())) {
                ((Player) e.getEntity()).setNoDamageTicks(20);
                e.setCancelled(true);
            }
        }
    }

}
