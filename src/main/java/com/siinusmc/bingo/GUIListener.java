package com.siinusmc.bingo;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        if(!Bingo.PLAYERS.contains(p)) {
            return;
        }
        if(e.getView().getTitle().equals("Bingo items")||e.getView().getTitle().startsWith("Recipe - ")) {
            if(e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta() || !(e.getCurrentItem().getItemMeta().getDisplayName().equals("§aErledigt!"))) {
                RecipeManager.showRecipe(e.getCurrentItem(),p);
            }
            e.setCancelled(true);
            return;
        }
        if(e.getClickedInventory()!=null&&e.getClickedInventory().getHolder()==p) {
            ItemListener.check(p,e.getCurrentItem());
        }
    }
}
