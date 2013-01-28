package bspkrs.blockbreaker;

import net.minecraftforge.common.Configuration;
import bspkrs.fml.util.Config;

public final class BBSettings
{
    public static final String MOD_VERSION_NUMBER = "1.4.6.r01";
    public static boolean      allowUpdateCheck   = true;
    // @MLProp(info="Block List, \';\' splits blocks and \',\' splits block ID and metadata.")
    public static String       blocks             = "17";
    // @MLProp(info="0-No drops. 1-Drops in all modes but Creative. 2-Drops in all modes.")
    public static int          drops              = 1;
    // @MLProp(info="Limit of blocks to be destroyed at once. Use -1 for infinite.")
    public static int          blockLimit         = 800;
    // @MLProp(info="Maximum distance from the player to search blocks to destroy. Use -1 for infinite.")
    public static int          maximumDistance    = 5;
    // @MLProp(info="Maximum number of blocks to be removed per game tick (1/20 seconds). Using a low number will keep the game from getting huge performance drops but also decreases the speed of which things are destroyed.\n\n**ONLY EDIT WHAT IS BELOW THIS**")
    public static int          blocksPerTick      = 50;
    
    public static int[][][]    blockGroups;
    
    public static void loadConfig(Configuration config)
    {
        String ctgyGen = Configuration.CATEGORY_GENERAL;
        
        config.load();
        allowUpdateCheck = Config.getBoolean(config, "allowUpdateCheck", ctgyGen, allowUpdateCheck, "Set to true to allow checking for mod updates, false to disable");
        // blocks = Config.getString(config, "blocks", ctgyGen, blocks, "");
        config.save();
    }
}
