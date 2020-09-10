package com.siinusmc.bingo;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.bukkit.Material.*;

public final class Items {
    public static final Material[] MATERIALS = {ENDER_CHEST,SPONGE,DISPENSER,NOTE_BLOCK,RAIL,COBWEB,SEA_PICKLE,CRIMSON_FUNGUS,WEEPING_VINES,BAMBOO,TNT,BOOKSHELF,OBSIDIAN,SNOW_BLOCK,JUKEBOX,PUMPKIN,SOUL_LANTERN,CHAIN,ENCHANTING_TABLE,TRIPWIRE_HOOK,ANVIL,DAYLIGHT_DETECTOR,TARGET,TERRACOTTA,SEA_LANTERN,MAGMA_BLOCK,NETHER_WART_BLOCK,BONE_BLOCK,SCAFFOLDING,DIAMOND_SWORD,DIAMOND_PICKAXE,DIAMOND_HELMET,NETHERITE_SWORD,NETHERITE_PICKAXE,NETHERITE_HELMET,NETHERITE_INGOT,SHIELD,GOLDEN_APPLE,WATER_BUCKET,COMPASS,CLOCK,CAKE,COOKIE,MELON_SLICE,FIRE_CHARGE,EXPERIENCE_BOTTLE,WRITABLE_BOOK,RABBIT_FOOT,NAME_TAG,END_CRYSTAL,SHROOMLIGHT,HONEY_BOTTLE,LODESTONE,CRYING_OBSIDIAN,RESPAWN_ANCHOR,ENDER_PEARL,ENDER_EYE,POLISHED_BLACKSTONE_BRICKS,IRON_BOOTS};
    public static final int COUNT = 56;
    
    public static final Material[] DIAMOND = {DIAMOND_SWORD,DIAMOND_PICKAXE,DIAMOND_AXE,DIAMOND_SHOVEL,DIAMOND_HOE,DIAMOND_HELMET,DIAMOND_CHESTPLATE,DIAMOND_LEGGINGS,DIAMOND_BOOTS};
    public static final Material[] NETHERITE = {NETHERITE_SWORD,NETHERITE_PICKAXE,NETHERITE_AXE,NETHERITE_SHOVEL,NETHERITE_HOE,NETHERITE_HELMET,NETHERITE_CHESTPLATE,NETHERITE_LEGGINGS,NETHERITE_BOOTS};
    
    private static final Random random=new Random();
    
    private static boolean toEnchant(Material m) {
        switch (m) {
            case SHIELD:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
            case IRON_BOOTS:
                return true;
            default:
                return false;
        }
    }
    
    private static Enchantment getEnchant(Material m) {
        Enchantment ench = m==IRON_BOOTS?Enchantment.SOUL_SPEED:Enchantment.DURABILITY;
        if(random.nextBoolean()) {
            switch (m) {
                case DIAMOND_SWORD:
                case NETHERITE_SWORD:
                    int r = random.nextInt(2);
                    ench=r>0?(r>1?Enchantment.DAMAGE_ALL:Enchantment.DAMAGE_UNDEAD):Enchantment.DAMAGE_ARTHROPODS;
                    break;
                case DIAMOND_PICKAXE:
                case DIAMOND_AXE:
                case DIAMOND_SHOVEL:
                case DIAMOND_HOE:
                case NETHERITE_PICKAXE:
                case NETHERITE_AXE:
                case NETHERITE_SHOVEL:
                case NETHERITE_HOE:
                    ench=Enchantment.DIG_SPEED;
                    break;
            }
        }
        return ench;
    }
    
    public static Material randomize(Material m) {
        switch (m) {
            case RAIL:
                int r1 = random.nextInt(2);
                return r1>0?(r1>1?POWERED_RAIL:ACTIVATOR_RAIL):DETECTOR_RAIL;
            case CRIMSON_FUNGUS:
                return random.nextBoolean()?CRIMSON_FUNGUS:WARPED_FUNGUS;
            case WEEPING_VINES:
                return random.nextBoolean()?TWISTING_VINES:WEEPING_VINES;
            case SOUL_LANTERN:
                return random.nextBoolean()?SOUL_LANTERN:SOUL_CAMPFIRE;
            case NETHER_WART_BLOCK:
                return random.nextBoolean()?NETHER_WART_BLOCK:WARPED_WART_BLOCK;
            case WATER_BUCKET:
                int r2 = random.nextInt(2);
                return r2>0?(r2>1?WATER_BUCKET:LAVA_BUCKET):MILK_BUCKET;
            case DIAMOND_SWORD:
            case DIAMOND_PICKAXE:
                return DIAMOND[random.nextInt(5)];
            case DIAMOND_HELMET:
                return DIAMOND[random.nextInt(4)+5];
            case NETHERITE_SWORD:
            case NETHERITE_PICKAXE:
                return NETHERITE[random.nextInt(5)];
            case NETHERITE_HELMET:
                return NETHERITE[random.nextInt(4)+5];
            default:
                return m;
        }
    }
    
    public static ItemStack[] generate(int n) {
        ItemStack[] g = new ItemStack[n];
        List<Material> ms = new ArrayList<>();
        for (int i=0; i<n; i++) {
            Material m = MATERIALS[random.nextInt(COUNT-1)];
            if(ms.contains(m)) {
                i--;
                continue;
            }
            ms.add(m);
            boolean te = toEnchant(m);
            m = randomize(m);
            ItemStack item = new ItemStack(m);
            if (te) {
                item.addEnchantment(getEnchant(m), 1);
            }
            g[i] = item.clone();
        }
        return g;
    }
}