package com.siinusmc.bingo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }
        if(!Bingo.RUNNING) {
            return false;
        }
        Player p = (Player) sender;
        if(!Bingo.PLAYERS.contains(p)) {
            p.sendMessage("§cYou have not joined a game yet!");
            return false;
        }
        if(p.getWorld().getName().equalsIgnoreCase("world_nether")||p.getWorld().getName().equalsIgnoreCase("world_the_end")) {
            p.teleport(Bukkit.getWorld("world").getSpawnLocation());
            return true;
        }
        Location l = p.getLocation().clone();
        l.setY(255);
        while (l.getBlock().getType()==Material.AIR) {
            l.setY(l.getY()-1);
        }
        l.setY(l.getY()+1);
        p.teleport(l);
        return true;
    }
}
