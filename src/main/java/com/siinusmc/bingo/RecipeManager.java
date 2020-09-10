package com.siinusmc.bingo;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RecipeManager {

    @SuppressWarnings("deprecated")
    public static void showRecipe(ItemStack result, Player p) {
        List<Recipe> recipes = Bukkit.getRecipesFor(result);
        if(recipes.size()==0) {
            return;
        }
        int cool = -20;
        int repeat = recipes.size()*20;
        BukkitScheduler s = Bukkit.getScheduler();
        for(Recipe r : recipes) {
            if(r instanceof ShapedRecipe) {
               // SafeRunnable shaped = new SafeRunnable() {
                //    @Override
                   // public void run() {
                       // if(!p.getOpenInventory().getTitle().equals("Recipe - "+result.getI18NDisplayName())) {
                        //    cancel();
                        //}
                        Inventory inv = Bukkit.createInventory(null,InventoryType.WORKBENCH,"Recipe - "+result.getI18NDisplayName());
                        inv.setItem(0,result);
                ItemStack[] matrix = getMatrix((ShapedRecipe) r);
                        for(int i=0; i<9; i++) {
                            if(matrix[i]!=null) {
                                inv.setItem(i+1,matrix[i]);
                            }
                        }
                        p.openInventory(inv);
                   // }
                //};
                //shaped.setTaskID(s.runTaskTimer(Bingo.getInstance(),shaped,cool+=20,repeat).getTaskId());
                //continue;
                return;
            }
            if(r instanceof ShapelessRecipe) {
                /*SafeRunnable shapeless = new SafeRunnable() {
                    @Override
                    public void run() {
                        if(!p.getOpenInventory().getTitle().equals("Recipe - "+result.getI18NDisplayName())) {
                            cancel();
                        }*/
                        Inventory inv = Bukkit.createInventory(null, InventoryType.WORKBENCH,"Recipe - "+result.getI18NDisplayName());
                        List<ItemStack> ing = ((ShapelessRecipe) r).getIngredientList();
                        ItemStack[] matrix = new ItemStack[ing.size()];
                        inv.setItem(0,result);
                        inv.addItem(((ShapelessRecipe) r).getIngredientList().toArray(matrix));
                        p.openInventory(inv);
                   // }
                //};
                //shapeless.setTaskID(s.runTaskTimer(Bingo.getInstance(),shapeless,cool+=20,repeat).getTaskId());
                //continue;
                return;
            }
            /*if(r instanceof CookingRecipe<?>) {
                /*SafeRunnable cooking = new SafeRunnable() {
                    @Override
                    public void run() {
                        if(!p.getOpenInventory().getTitle().equals("Recipe - "+result.getI18NDisplayName())) {
                            cancel();
                        }
                        Inventory inv = Bukkit.createInventory(null,InventoryType.FURNACE,"Recipe - "+result.getI18NDisplayName());
                        inv.setItem(0,((CookingRecipe<?>) r).getInput());
                        inv.setItem(2,result);
                        p.openInventory(inv);
                    //}
                //};
                //cooking.setTaskID(s.runTaskTimer(Bingo.getInstance(),cooking,cool+=20,repeat).getTaskId());
                continue;
            }
            if(r instanceof SmithingRecipe) {
                /*SafeRunnable smithing = new SafeRunnable() {
                    @Override
                    public void run() {
                        if(!p.getOpenInventory().getTitle().equals("Recipe - "+result.getI18NDisplayName())) {
                            cancel();
                        }
                        Inventory inv = Bukkit.createInventory(null,InventoryType.SMITHING,"Recipe - "+result.getI18NDisplayName());
                        inv.setItem(0,((SmithingRecipe) r).getBase().getItemStack());
                        inv.setItem(1,((SmithingRecipe) r).getAddition().getItemStack());
                        inv.setItem(2,result);
                        p.openInventory(inv);
                    //}
                //};
                //smithing.setTaskID(s.runTaskTimer(Bingo.getInstance(),smithing,cool+=20,repeat).getTaskId());
                continue;
            }*/
        }
    }
    
    public static ItemStack[] getMatrix(ShapedRecipe r) {
        Map<Character,ItemStack> im = r.getIngredientMap();
        String[] rows = r.getShape();
        //System.out.println(Arrays.toString(rows));
        char[][] chars = new char[3][3];
        for(int i=0; i<rows.length; i++) {
            char[] str_chars = rows[i].toCharArray();
            chars[i] = str_chars;
        }
        ItemStack[] matrix = new ItemStack[9];
        int index = 0;
        for(char[] cs : chars) {
            for(char c : cs) {
                matrix[index++] = im.get(c);
            }
            while(index%3>0) {
                matrix[index++] = null;
            }
        }
        /*for(ItemStack its : matrix) {
            if
        }*/
        return matrix;
    }

    public static abstract class SafeRunnable implements Runnable{
        private int taskID;

        public void cancel() {
            Bukkit.getScheduler().cancelTask(taskID);
        }

        public void setTaskID(int taskID) {
            this.taskID = taskID;
        }
    }
}