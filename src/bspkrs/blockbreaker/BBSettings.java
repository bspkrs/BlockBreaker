package bspkrs.blockbreaker;

import java.io.File;

import net.minecraftforge.common.Configuration;
import bspkrs.fml.util.Config;
import bspkrs.util.CommonUtils;

/*
 * @Author bspkrs
 */

public final class BBSettings
{
    public static final String  MOD_VERSION_NUMBER = "1.5.0.r01";
    public static int[][][]     blockGroups;

    public static Configuration config;

    // Config file fields
    public final static String  blockListDesc      = "Block List, \";\" splits blocks and \",\" splits block ID and metadata.";
    public static String        blockList          = "17;";
    public final static String  itemDropModeDesc   = "Item drops values: 0-No drops. 1-Drops in Survival. 2-Drops in Survival & Creative. 3-Drops in Survival, Creative, & Adventure";
    public static int           itemDropMode       = 1;
    public final static String  blockLimitDesc     = "Limit of blocks to be destroyed at once. Use -1 for infinite.";
    public static int           blockLimit         = 800;
    public final static String  maxDistanceDesc    = "Maximum distance from the first block to search for blocks to destroy. Use -1 for infinite.";
    public static int           maxDistance        = 6;
    public final static String  blocksPerTickDesc  = "Maximum number of blocks to be removed per game tick (1/20 seconds). Using a low number will keep the game from getting huge" +
                                                           "performance drops but also decreases the speed at which blocks are destroyed.";
    public static int           blocksPerTick      = 50;
    public final static String  sneakActionDesc    = "Set sneakAction = \"disable\" to disable the block breaker effect while sneaking,\n" +
                                                           "set sneakAction = \"enable\" to only enable the block breaker effect while sneaking,\n" +
                                                           "set sneakAction = \"none\" to have the block breaker effect enabled regardless of sneaking.";
    public static String        sneakAction        = "disable";

    public static void loadConfig(File file)
    {
        String ctgyGen = Configuration.CATEGORY_GENERAL;

        //        if (Block.class.getSimpleName().equalsIgnoreCase("Block"))
        //        { // debug settings for deobfuscated execution
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
        //            blockLimit = -1;
        //            maxDistance = 30;
        //            blocksPerTick = 666;
        //            sneakAction = "disable";
        //            if (file.exists())
        //                file.delete();
        //        }

        config = new Configuration(file);

        config.load();

        blockList = Config.getString(config, "blockList", ctgyGen, blockList, blockListDesc);
        itemDropMode = Config.getInt(config, "itemDropMode", ctgyGen, itemDropMode, 0, 3, itemDropModeDesc);
        blockLimit = Config.getInt(config, "blockLimit", ctgyGen, blockLimit, -1, Integer.MAX_VALUE, blockLimitDesc);
        maxDistance = Config.getInt(config, "maxDistance", ctgyGen, maxDistance, -1, 1000, maxDistanceDesc);
        blocksPerTick = Config.getInt(config, "blocksPerTick", ctgyGen, blocksPerTick, 1, 1000, blocksPerTickDesc);
        sneakAction = Config.getString(config, "sneakAction", ctgyGen, sneakAction, sneakActionDesc);

        config.save();

        if (blockGroups == null)
            blockGroups = CommonUtils.stringToGroups(blockList);
    }
}
