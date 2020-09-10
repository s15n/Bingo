package com.siinusmc.bingo;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public final class BingoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player p = (Player) sender;
        if (args.length < 1) {
            if (!Bingo.PLAYERS.contains(p)) {
                p.sendMessage("§bYou have not joined the game yet!");
                return false;
            }
            if (Bingo.RUNNING) {
                ItemListener.check(p, p.getInventory());
                openMenu(p);
            } else {
                p.sendMessage("§cNo bingo game started yet!");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (p.isOp()) {
                if (Bingo.RUNNING) {
                    p.sendMessage("§cAlready running!");
                } else {
                    p.sendMessage("§aStarted a new game!");
                    if (args.length > 1) {
                        try {
                            startGame(Math.max(1, Math.min(54, Integer.parseInt(args[1]))));
                        } catch (NumberFormatException e) {
                            p.sendMessage("§72nd argument was not a number, setting to 9");
                            startGame(9);
                        }
                    } else {
                        startGame(9);
                    }
                }
                return true;
            } else {
                p.sendMessage("§cYou need to be an operator!");
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (p.isOp()) {
                if (Bingo.RUNNING) {
                    p.sendMessage("§bShutting down bingo...");
                    stopGame();
                } else {
                    p.sendMessage("§cNo game running!");
                }
                return true;
            } else {
                p.sendMessage("§cYou need to be an operator!");
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("join")) {
            if (args.length < 2) {
                if (Bingo.PLAYERS.contains(p)) {
                    p.sendMessage("§eYou have already joined the game!");
                    return false;
                }
                int size = Bingo.TEAMS.size();
                BingoTeam newTeam = new BingoTeam(p);
                Bingo.PLAYERS.add(p);
                Bingo.TEAMS.add(newTeam);
                p.sendMessage("§aYou are now in team §f#§b" + (size + 1) + "!");
                return true;
            }
            try {
                int id = Integer.parseInt(args[1]);
                Bingo.TEAMS.get(id - 1).players.add(p);
                Bingo.PLAYERS.add(p);
                p.sendMessage("§aYou are now in team §f#§b" + id + "!");
                return true;
            } catch (NumberFormatException e) {
                p.sendMessage("§72nd argument was not a number, ignoring it!");
                p.performCommand("bingo join");
                return false;
            } catch (IndexOutOfBoundsException e) {
                p.sendMessage("§cThis team does not exist!");
                return false;
            } finally {
                ScoreboardManager.update();
            }
        }
        if (args[0].equalsIgnoreCase("leave")) {
            if (Bingo.PLAYERS.contains(p)) {
                Bingo.PLAYERS.remove(p);
                BingoTeam t = TeamManager.getTeam(p);
                assert t != null;
                t.players.remove(p);
                p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                p.sendMessage("§eYou left the game!");
                return true;
            }
            p.sendMessage("§eYou have not joined the game!");
            return false;
        }
        if (args[0].equalsIgnoreCase("debug")) {
            p.sendMessage("PLAYERS.size - " + Bingo.PLAYERS.size());
            p.sendMessage("TEAMS.size - " + Bingo.TEAMS.size());
            for (Player pl : Bukkit.getOnlinePlayers()) {
                p.sendMessage("§e" + pl.getName());
                p.sendMessage("  PLAYERS.contains - " + Bingo.PLAYERS.contains(pl));
                BingoTeam t = TeamManager.getTeam(pl);
                if (t != null) {
                    t.calculateIndex();
                    p.sendMessage("  team - " + t.index);
                    p.sendMessage("  team.players.size - " + t.players.size());
                    p.sendMessage("  team.items.size - " + t.items.size());
                    p.sendMessage("  team.rank - " + t.rank);
                    p.sendMessage("  team.count - " + t.count);
                } else {
                    p.sendMessage("  team - null");
                }
            }
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 2) {
                p.sendMessage("§cNo index number!");
                return false;
            }
            if (!p.isOp()) {
                p.sendMessage("§cYou need to be an operator!");
                return false;
            }
            try {
                int index = Integer.parseInt(args[1]);
                if (args.length < 3) {
                    Bingo.ITEMS[index] = p.getInventory().getItemInMainHand();
                    return true;
                }
                Material material = Material.valueOf(args[2].toUpperCase());
                Bingo.ITEMS[index] = new ItemStack(material);
                return true;
            } catch (NumberFormatException e) {
                p.sendMessage("§c...was not a number!");
                return false;
            } catch (EnumConstantNotPresentException e) {
                p.sendMessage("§c...was not a material!");
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("save")) {
            if (!p.isOp()) {
                p.sendMessage("§cYou need to be an operator!");
                return false;
            }
            String dirName = "./plugins/bingo/";
            String fileName;
            if (args.length < 2) {
                fileName = dirName + (Bingo.SAVE_NAME==null?System.currentTimeMillis()+"":Bingo.SAVE_NAME) + ".json";
            } else {
                fileName = dirName + (Bingo.SAVE_NAME = args[1]) + ".json";
            }
            return saveData(p, dirName, fileName, true);
        }
        if (args[0].equalsIgnoreCase("load")) {
            if (args.length < 2) {
                p.sendMessage("§cPlease provide a file name!");
                return false;
            }
            if (!p.isOp()) {
                p.sendMessage("§cYou need to be an operator!");
                return false;
            }
            File readFile = new File("./plugins/bingo/" + args[1] + ".json");
            if(Bingo.RUNNING) {
                p.sendMessage("§ePlease stop the running game before loading a new one!");
                return false;
            }
            Bingo.RUNNING = true;
            Bingo.AUTOSAVE = false;
            Bingo.SAVE_NAME = null;
            clearTags();
            Bingo.TEAMS = new ArrayList<>();
            Bingo.PLAYERS = new ArrayList<>();
            Bingo.SCOREBOARD = new ScoreboardManager();
            ScoreboardManager.update();
            return loadData(p, readFile, true);
        }
        if (args[0].equalsIgnoreCase("autosave")) {
            boolean on = !Bingo.AUTOSAVE;
            if(args.length>1) {
                if(args[1].equalsIgnoreCase("on")) on = true;
                if(args[1].equalsIgnoreCase("off")) on = false;
                if(args[1].equalsIgnoreCase("name")) {
                    if(args.length>2) {
                        Bingo.SAVE_NAME=args[2];
                        p.sendMessage("§aSet save file name to:"+Bingo.SAVE_NAME);
                        return true;
                    }
                }
            }
            p.sendMessage("§aAutosave is now §b"+(on?"activated":"deactivated")+"!");
            Bingo.AUTOSAVE = on;
        }
        return false;
    }

    private void openMenu(Player p) {
        BingoTeam t = TeamManager.getTeam(p);
        if (t == null) {
            return;
        }
        Inventory menu = Bukkit.createInventory(null, ((Bingo.COUNT / 9) * 9) + (Bingo.COUNT % 9 == 0 ? 0 : 9), "Bingo items");
        menu.setMaxStackSize(1);
        for (int i = 0; i < Bingo.COUNT; i++) {
            ItemStack item;
            if (t.items.contains(i)) {
                item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§aErledigt!");
                meta.setLore(new ArrayList<>(Collections.singleton("§7" + Bingo.ITEMS[i].getI18NDisplayName())));
                item.setItemMeta(meta);
            } else {
                item = new ItemStack(Bingo.ITEMS[i]);
            }
            menu.addItem(item);
        }
        p.openInventory(menu);
    }

    @SuppressWarnings("unchecked")
    static boolean saveData(Player p, String dirName, String fileName, boolean msg) {
        File directory = new File(dirName);
        File saveFile = new File(fileName);
        try {
            if (directory.mkdirs()) {
                if(msg) p.sendMessage("§7Created directories...");
            }
            if (saveFile.createNewFile()) {
                if(msg) p.sendMessage("§7Created file...");
            } else {
                if(msg) p.sendMessage("§7File already exists...");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        String jsonText;
        JSONObject root = new JSONObject();
        JSONArray items = new JSONArray();
        for (ItemStack item : Bingo.ITEMS) {
            JSONObject jItem = new JSONObject();
            jItem.put("material",item.getType().toString());
            Set<Enchantment> enchs = item.getEnchantments().keySet();
            if(!enchs.isEmpty()) {
                jItem.put("enchantment",enchs.iterator().next().getKey().toString());
            }
            items.add(jItem);
        }
        root.put("items",items);
        JSONArray teams = new JSONArray();
        for(BingoTeam team : Bingo.TEAMS) {
            JSONArray jTeam = new JSONArray();
            jTeam.addAll(team.items);
            teams.add(jTeam);
        }
        root.put("teams",teams);
        jsonText=root.toJSONString();
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(jsonText);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if(msg) p.sendMessage("§aSuccessfully saved data!");
        if(msg) p.sendMessage("§7Location: "+fileName);
        return true;
    }

    static boolean loadData(Player p, File readFile, boolean msg) {
        StringBuilder data = new StringBuilder();
        try (Scanner reader = new Scanner(readFile)) {
            if(msg) p.sendMessage("§7Reading data...");
            while (reader.hasNextLine()) {
                data.append(reader.nextLine());
            }
        } catch (FileNotFoundException e) {
            if(msg) p.sendMessage("§4File not found!");
            e.printStackTrace();
            return false;
        }
        String dataString = data.toString();
        JSONParser parser = new JSONParser();
        try {
            if(msg) p.sendMessage("§7Parsing data...");
            Object dataObject = parser.parse(dataString);
            if(!(dataObject instanceof JSONObject)) {
                if(msg) p.sendMessage("§4Error at parsing data! §cDataObject is not a JSON Object");
                return false;
            }
            if(((JSONObject)dataObject).containsKey("items")) {
                Object itemsObject = ((JSONObject) dataObject).get("items");
                if(!(itemsObject instanceof JSONArray)) {
                    p.sendMessage("§4Error at parsing data! §cItemsObject is not a JSON Array");
                    return false;
                }
                ItemStack[] items = new ItemStack[((JSONArray) itemsObject).size()];
                int itemIndex = 0;
                for(Object item : (JSONArray) itemsObject) {
                    ItemStack itemStack = null;
                    if(!(item instanceof JSONObject)) {
                        continue;
                    }
                    if(((JSONObject) item).containsKey("material")) {
                        Object material = ((JSONObject) item).get("material");
                        if(!(material instanceof String)) {
                            p.sendMessage("§4Error at parsing data! §cMaterial is not a String");
                            continue;
                        }
                        try {
                            itemStack = new ItemStack(Material.valueOf((String) material));
                        } catch (IllegalArgumentException e) {
                            p.sendMessage("§4Error at parsing data! §cMaterial is not a Material");
                            continue;
                        }
                    }
                    if(((JSONObject) item).containsKey("enchantment")) {
                        Object ench = ((JSONObject) item).get("enchantment");
                        if(!(ench instanceof String)) {
                            p.sendMessage("§4Error at parsing data! §cEnchantment is not a String");
                            continue;
                        }
                        assert itemStack!=null;
                        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(((String) ench).split(":")[1]));
                        if(enchantment==null) {
                            p.sendMessage("§4Error at parsing data! §cEnchantment is not an Enchantment");
                            continue;
                        }
                        itemStack.addEnchantment(enchantment,1);
                    }
                    assert itemStack != null;
                    items[itemIndex++] = itemStack.clone();
                }
                Bingo.COUNT = items.length;
                Bingo.ITEMS = items;
                if(msg) p.sendMessage("§aLoaded items!");
            }
            if(((JSONObject) dataObject).containsKey("teams")) {
                Object teamsObject = ((JSONObject) dataObject).get("teams");
                if(!(teamsObject instanceof JSONArray)) {
                    p.sendMessage("§4Error at parsing data! §cTeamsObject is not a JSON Array");
                    return false;
                }
                for(Object team : (JSONArray) teamsObject) {
                    if(!(team instanceof JSONArray)) {
                        p.sendMessage("§4Error at parsing data! §cTeam is not a JSON Array");
                        continue;
                    }
                    BingoTeam bingoTeam = new BingoTeam();
                    for(Object item : (JSONArray) team) {
                        if(!(item instanceof Long)) {
                            p.sendMessage("§4Error at parsing data! §cItem is not a Long");
                            continue;
                        }
                        bingoTeam.items.add(((Long) item).intValue());
                        bingoTeam.count++;
                    }
                    bingoTeam.calculateIndex();
                    Bingo.TEAMS.add(bingoTeam);
                }
                p.sendMessage("§aLoaded teams!");
            }
        } catch (ParseException e) {
            p.sendMessage("§4Could not parse data!");
            e.printStackTrace();
        }

        return true;
    }

    static void startGame(int n) {
        Bingo.RUNNING = true;
        Bingo.AUTOSAVE = false;
        Bingo.SAVE_NAME = null;
        Bingo.COUNT = n;
        Bingo.ITEMS = Items.generate(n);
        clearTags();
        /*for(OfflinePlayer p : Bingo.PLAYERS) {
            if(p instanceof Player) {
                ((Player) p).setMetadata("bingo_count", new FixedMetadataValue(Bingo.getInstance(), 0));
            }
        }*/
        Bingo.TEAMS = new ArrayList<>();
        Bingo.PLAYERS = new ArrayList<>();
        Bingo.SCOREBOARD = new ScoreboardManager();
        ScoreboardManager.update();
    }

    static void stopGame() {
        Bingo.RUNNING = false;
        Bingo.COUNT = 0;
        Bingo.ITEMS = new ItemStack[]{};
        clearTags();
        Bingo.TEAMS = new ArrayList<>();
        Bingo.PLAYERS = new ArrayList<>();
        Bingo.SAVE_NAME = null;
        Bingo.AUTOSAVE = false;
        ScoreboardManager.hide();
    }

    static void clearTags() {
        for (OfflinePlayer p : Bingo.PLAYERS) {
            if (p instanceof Player) {
                List<String> st = new ArrayList<>(((Player) p).getScoreboardTags());
                for (String s : st) {
                    if (s.startsWith("bingo_")) {
                        ((Player) p).removeScoreboardTag(s);
                    }
                }
                ((Player) p).removeMetadata("bingo_count", Bingo.getInstance());
            }
        }
    }
}