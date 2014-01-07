package bspkrs.blockbreaker;

import java.io.File;

import bspkrs.util.CommonUtils;
import bspkrs.util.Configuration;

/*
 * @Author bspkrs
 */

public final class BBSettings
{
    public static int[][][]     blockGroups;
    
    public static Configuration config;
    
    // Config file fields
    public final static String  allowItemDamageDesc  = "Enable to cause item damage based on number of blocks destroyed.";
    public static boolean       allowItemDamage      = true;
    public final static String  blockListDesc        = "Block List, \";\" splits blocks and \",\" splits block ID and metadata.";
    public static String        blockList            = "17;";
    public final static String  itemDropModeDesc     = "Item drops values: 0-No drops. 1-Drops in Survival. 2-Drops in Survival & Creative. 3-Drops in Survival, Creative, & Adventure";
    public static int           itemDropMode         = 1;
    public final static String  itemsDropInPlaceDesc = "When true items will be dropped at the coordinate of the block. Otherwise they will be grouped at the starting position.";
    public static boolean       itemsDropInPlace     = false;
    public final static String  blockLimitDesc       = "Limit of blocks to be destroyed at once. Use -1 for infinite.";
    public static int           blockLimit           = 800;
    public final static String  maxDistanceDesc      = "Maximum distance from the first block to search for blocks to destroy. Use -1 for infinite.";
    public static int           maxDistance          = 6;
    public final static String  breakShapeDesc       = "The shape that the block breaking algorithm will use when applying the max distance. Valid values are \"cubic\" and \"spherical\".";
    public static String        breakShape           = "spherical";
    public final static String  blocksPerTickDesc    = "Maximum number of blocks to be removed per game tick (~1/20 of a second). Using a low number will keep the game from getting huge" +
                                                             "performance drops but also decreases the speed at which blocks are destroyed.";
    public static int           blocksPerTick        = 50;
    public static String[]      sneakActions         = { "disable", "enable", "none" };
    public final static String  sneakActionDesc      = String.format("Set sneakAction = \"%s\" to disable the block breaker effect while sneaking,\n" +
                                                             "set sneakAction = \"%s\" to only enable the block breaker effect while sneaking,\n" +
                                                             "set sneakAction = \"%s\" to have the block breaker effect enabled regardless of sneaking.", sneakActions[0], sneakActions[1], sneakActions[2]);
    public static String        sneakAction          = sneakActions[0];
    
    public static void loadConfig(File file)
    {
        String ctgyGen = Configuration.CATEGORY_GENERAL;
        
        if (!CommonUtils.isObfuscatedEnv())
        { // debug settings for deobfuscated execution
        //            blockList = "" +
        //                    Block.stone.blockID + ";" +
        //                    Block.dirt.blockID + ";" +
        //                    Block.gravel.blockID + ";" +
        //                    Block.glowStone.blockID + ";" +
        //                    Block.oreCoal.blockID + ";" +
        //                    Block.oreDiamond.blockID + ";" +
        //                    Block.oreEmerald.blockID + ";" +
        //                    Block.oreGold.blockID + ";" +
        //                    Block.oreIron.blockID + ";" +
        //                    Block.oreLapis.blockID + ";" +
        //                    Block.oreRedstone.blockID + ";" +
        //                    Block.netherrack.blockID + ";" +
        //                    Block.oreRedstoneGlowing.blockID + ";";
        //            itemDropMode = 1;
        //            itemsDropInPlace = true;
        //            blockLimit = -1;
        //            maxDistance = 200;
        //            blocksPerTick = 666;
        //            sneakAction = "disable";
        //            breakShape = "spherical";
        //            if (file.exists())
        //                file.delete();
        }
        
        config = new Configuration(file);
        
        config.load();
        
        blockList = config.getString("blockList", ctgyGen, blockList, blockListDesc);
        itemDropMode = config.getInt("itemDropMode", ctgyGen, itemDropMode, 0, 3, itemDropModeDesc);
        itemsDropInPlace = config.getBoolean("itemsDropInPlace", ctgyGen, itemsDropInPlace, itemsDropInPlaceDesc);
        allowItemDamage = config.getBoolean("allowItemDamage", ctgyGen, allowItemDamage, allowItemDamageDesc);
        blockLimit = config.getInt("blockLimit", ctgyGen, blockLimit, -1, Integer.MAX_VALUE, blockLimitDesc);
        maxDistance = config.getInt("maxDistance", ctgyGen, maxDistance, -1, 1000, maxDistanceDesc);
        breakShape = config.getString("breakShape", ctgyGen, breakShape, breakShapeDesc);
        blocksPerTick = config.getInt("blocksPerTick", ctgyGen, blocksPerTick, 1, 1000, blocksPerTickDesc);
        sneakAction = config.getString("sneakAction", ctgyGen, sneakAction, sneakActionDesc);
        
        config.save();
        
        if (blockGroups == null)
            blockGroups = CommonUtils.stringToGroups(blockList);
    }
}
