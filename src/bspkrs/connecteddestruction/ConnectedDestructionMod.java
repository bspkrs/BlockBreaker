package bspkrs.connecteddestruction;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import bspkrs.fml.util.Config;
import bspkrs.util.ModVersionChecker;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.PlayerControllerMP;
import net.minecraftforge.common.Configuration;

@Mod(modid="ConnectedDestruction", version = "1.4.2.r01", useMetadata=true)
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class ConnectedDestructionMod
{
    private static ModVersionChecker versionChecker;
    private String versionURL = "https://dl.dropbox.com/u/20748481/Minecraft/1.4.2/connectedDestructionForge.version";
    private String mcfTopic = "http://www.minecraftforum.net/topic/1009577-";

    @SideOnly(Side.CLIENT)
    public static Minecraft mcClient;

    public ModMetadata metadata;
    
	public static boolean allowUpdateCheck;
   // @MLProp(info="Block List, \';\' splits blocks and \',\' splits block ID and metadata.")
    public static String blocks = "17";
   // @MLProp(info="0-No drops. 1-Drops in all modes but Creative. 2-Drops in all modes.")
    public static int drops = 1;
   // @MLProp(info="Limit of blocks to be destroyed at once. Use -1 for infinite.")
    public static int blockLimit = 800;
   // @MLProp(info="Maximum distance from the player to search blocks to destroy. Use -1 for infinite.")
    public static int maximumDistance = 5;
   // @MLProp(info="Maximum number of blocks to be removed per game tick (1/20 seconds). Using a low number will keep the game from getting huge performance drops but also decreases the speed of which things are destroyed.\n\n**ONLY EDIT WHAT IS BELOW THIS**")
    public static int blocksPerTick = 50;
    
    public int[][][] blockGroups;
            
    public ConnectedDestructionMod()
    {}

    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        metadata = event.getModMetadata();
        versionChecker = new ModVersionChecker(metadata.name, metadata.version, versionURL, mcfTopic, FMLLog.getLogger());
        versionChecker.checkVersionWithLogging();
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        String ctgyGen = Configuration.CATEGORY_GENERAL;
        
        config.load();
        allowUpdateCheck = Config.getBoolean(config, "allowUpdateCheck", ctgyGen, allowUpdateCheck, "Set to true to allow checking for mod updates, false to disable");
        blocks = Config.getString(config, "blocks", ctgyGen, blocks, "");
        config.save();
    }
    
    public boolean onTick(float f, Minecraft minecraft)
    {
        if(blockGroups == null) blockGroups = stringToGroups(blocks);
        minecraft.getIntegratedServer().worldServerForDimension(minecraft.thePlayer.dimension).getPlayerEntityByName(minecraft.thePlayer.username);
        if(isControlled(minecraft.playerController)) return true;
        try 
        {
                NetClientHandler nch = (NetClientHandler)ModLoader.getPrivateValue(PlayerControllerMP.class, minecraft.playerController, 1);
                minecraft.playerController = new ConnectedDestructionMP(minecraft, nch);
        } 
        catch (Throwable e) 
        {
            e.printStackTrace();
        }
        return true;
    }
    

    @SideOnly(Side.CLIENT)
    public static boolean onClientTick(TickType tick, boolean isStart)
    {
        if (isStart) {
            return true;
        }

        if (mcClient != null && mcClient.thePlayer != null)
        {
            if(allowUpdateCheck)
                if(!versionChecker.isCurrentVersion())
                    for(String msg : versionChecker.getInGameMessage())
                        mcClient.thePlayer.addChatMessage(msg);
            return false;
        }

        return true;
    }
    
    public boolean isControlled(PlayerControllerMP pc)
    {
        return (pc instanceof ConnectedDestructionMP);
    }
    
    public boolean isIDInList(String list, int id, int metadata)
    {
        String[] itemArray = list.split(";");
        for(int i = 0; i < itemArray.length; i++)
        {
            String[] values = itemArray[i].split(",");
            int tempID = parseInt(values[0]);
            if(values.length > 1 && parseInt(values[1]) == metadata) return true;
            else if(tempID == id) return true;
        }
        return false;
    }
    
    private int parseInt(String string)
    {
        int res = 0;
        try
        {
            res = Integer.parseInt(string.trim());
        }
        catch(NumberFormatException e)
        {
            ModLoader.getLogger().log(Level.WARNING, "Invalid number format in string " + string);
        }
        return res;
    }
    
    public int[][][] stringToGroups(String string){
        List<int[][]> groupList = new ArrayList<int[][]>();
        String[] groups = string.split(";");
        for(String group: groups)
        {
            groupList.add(stringToGroup(group));
        }
        int[][][] res = new int[groupList.size()][][];
        for(int i = 0; i < groupList.size(); i++)
        {
            res[i] = groupList.get(i);
        }
        return res;
    }
    
    public int[][] stringToGroup(String string)
    {
        List<int[]> blockList = new ArrayList<int[]>();
        String[] blocks = string.split(">");
        for(String block: blocks){
            blockList.add(stringToBlock(block));
        }
        int[][] res = new int[blockList.size()][];
        for(int i = 0; i < blockList.size(); i++){
            res[i] = blockList.get(i);
        }
        return res;
    }
    
    public int[] stringToBlock(String string)
    {
        int[] values = new int[]{0, -1};
        String[] src = string.split(",");
        if(src.length < 1)
            return values;
        values[0] = parseInt(src[0]);
        if(src.length < 2)
            return values;
        values[1] = parseInt(src[1]);
        return values;
    }
    
    public boolean isBlockInGroups(int[] block, int[][][] groups)
    {
        for(int[][] group: groups)
        {
            if(indexOfBlock(block, group) > -1)
                return true;
        }
        return false;
    }
    
    public int indexOfBlock(int[] block, int[][] group)
    {
        for(int i = 0; i < group.length; i++)
        {
            if(block[0] == group[i][0])
            {
                if(group[i][1] == -1 || block[1] == group[i][1]) return i;
            }
        }
        return -1;
    }
    
    public int[][] getRelatedBlocks(int[] block, int[][][] groups)
    {
        List<int[]> blockList = new ArrayList<int[]>();
        for(int[][] group: groups)
        {
            if(indexOfBlock(block, group) > -1)
            {
                for(int i = 0; i < groups.length; i++)
                {
                    if(blockList.contains(group[i])) continue;
                    blockList.add(i, group[i]);
                }
            }
        }
        int[][] secondary = new int[blockList.size()][];
        for(int i = 0; i < blockList.size(); i++)
        {
            secondary[i] = blockList.get(i);
        }
        return secondary;
    }
    
    public int smallerBlockIndex(int[] block, int[][][] groups)
    {
        int min = Integer.MAX_VALUE;
        for(int[][] group: groups)
        {
            int i = indexOfBlock(block, group);
            if(i > -1 && i < min) min = i;
        }
        if(min == Integer.MAX_VALUE) min = -1;
        return min;
    }
    
    public boolean isMetadataNull(int id, int[][][] groups) 
    {
        for(int[][] group: groups)
        {
            for(int[] block: group)
            {
                if(block[0] == id && block[1] == -1) return true;
            }
        }
        return false;
    }
}
