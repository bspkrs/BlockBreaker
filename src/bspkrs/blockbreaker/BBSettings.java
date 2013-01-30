package bspkrs.blockbreaker;

import net.minecraftforge.common.Configuration;
import bspkrs.fml.util.Config;
import bspkrs.util.CommonUtils;

public final class BBSettings
{
    public static final String MOD_VERSION_NUMBER   = "1.4.6.r01";
    public static int[][][]    blockGroups;
    
    // Config file fields
    public final static String allowUpdateCheckDesc = "Set to true to allow checking for mod updates, false to disable";
    public static boolean      allowUpdateCheck     = true;
    public final static String blockListDesc        = "Block List, \";\" splits blocks and \",\" splits block ID and metadata.";
    public static String       blockList            = "17;";
    public final static String itemDropModeDesc     = "Item drops values: 0-No drops. 1-Drops in Survival. 2-Drops in Survival & Creative. 3-Drops in Survival, Creative, & Adventure";
    public static int          itemDropMode         = 1;
    public final static String blockLimitDesc       = "Limit of blocks to be destroyed at once. Use -1 for infinite.";
    public static int          blockLimit           = 800;
    public final static String maxDistanceDesc      = "Maximum distance from the first block to search for blocks to destroy. Use -1 for infinite.";
    public static int          maxDistance          = 6;
    public final static String blocksPerTickDesc    = "Maximum number of blocks to be removed per game tick (1/20 seconds). Using a low number will keep the game from getting huge" +
                                                            "performance drops but also decreases the speed at which blocks are destroyed.";
    public static int          blocksPerTick        = 50;
    public final static String sneakActionDesc      = "Set sneakAction = \"disable\" to disable the block breaker effect while sneaking,\n" +
                                                            "set sneakAction = \"enable\" to only enable the block breaker effect while sneaking,\n" +
                                                            "set sneakAction = \"none\" to have the block breaker effect enabled regardless of sneaking.";
    public static String       sneakAction          = "disable";
    
    public static void loadConfig(Configuration config)
    {
        String ctgyGen = Configuration.CATEGORY_GENERAL;
        
        config.load();
        
        allowUpdateCheck = Config.getBoolean(config, "allowUpdateCheck", ctgyGen, allowUpdateCheck, allowUpdateCheckDesc);
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
