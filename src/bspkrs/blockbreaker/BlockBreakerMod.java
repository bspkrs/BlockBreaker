package bspkrs.blockbreaker;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import bspkrs.treecapitator.fml.ClientPacketHandler;
import bspkrs.treecapitator.fml.ConnectionHandler;
import bspkrs.treecapitator.fml.ServerPacketHandler;
import bspkrs.treecapitator.fml.TreeCapitatorServer;
import bspkrs.util.BlockID;
import bspkrs.util.ModVersionChecker;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.IMCCallback;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;

@Mod(name = "BlockBreaker", modid = "BlockBreaker", version = "Forge " + BBSettings.MOD_VERSION_NUMBER, useMetadata = true)
@NetworkMod(clientSideRequired = false, serverSideRequired = false,
        clientPacketHandlerSpec = @SidedPacketHandler(channels = { "BlockBreaker" }, packetHandler = ClientPacketHandler.class),
        serverPacketHandlerSpec = @SidedPacketHandler(channels = { "BlockBreaker" }, packetHandler = ServerPacketHandler.class),
        connectionHandler = ConnectionHandler.class)
public class BlockBreakerMod
{
    private static ModVersionChecker versionChecker;
    private String                   versionURL      = "https://dl.dropbox.com/u/20748481/Minecraft/1.4.6/connectedDestruction.version";
    private String                   mcfTopic        = "http://www.minecraftforum.net/topic/1009577-";
    
    public ModMetadata               metadata;
    
    public static boolean            isCoreModLoaded = false;
    
    @SidedProxy(clientSide = "bspkrs.blockbreaker.ClientProxy", serverSide = "bspkrs.blockbreaker.CommonProxy")
    public static CommonProxy        proxy;
    
    @Instance(value = "BlockBreaker")
    public static BlockBreakerMod    instance;
    
    private static Loader            loader;
    
    public BlockBreakerMod()
    {
        loader = Loader.instance();
    }
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        metadata = event.getModMetadata();
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        
        if (BBSettings.allowUpdateCheck)
        {
            versionChecker = new ModVersionChecker(metadata.name, metadata.version, versionURL, mcfTopic, FMLLog.getLogger());
            versionChecker.checkVersionWithLogging();
        }
    }
    
    @Init
    public void init(FMLInitializationEvent event)
    {
        proxy.onLoad();
    }
    
    @IMCCallback
    public void processIMCMessages(IMCEvent event)
    {   
        
    }
    
    @PostInit
    public void postInit(FMLPostInitializationEvent event)
    {   
        
    }
    
    @ServerStarted
    public void serverStarted(FMLServerStartedEvent event)
    {
        new TreeCapitatorServer();
    }
    
    public void onBlockHarvested(World world, int x, int y, int z, Block block, int metadata, EntityPlayer player)
    {
        if (proxy.isEnabled())
        {
            BlockID blockID = new BlockID(block, metadata);
            
            ItemStack item = player.getCurrentEquippedItem();
            boolean itemCanHarvestBlock = (item != null) && (item.canHarvestBlock(block));
            
            if (ModLoader.getMinecraftInstance().isSingleplayer())
            {
                
                if (player.capabilities.isCreativeMode)
                {
                    if (itemCanHarvestBlock && BlockBreakerMod.instance.isBlockInGroups(new int[] { blockID.id, metadata },
                            BBSettings.blockGroups))
                    {
                        if (BlockBreakerMod.instance.isMetadataNull(blockID.id, BBSettings.blockGroups))
                            blockID = new BlockID(blockID.id);
                        
                        BlockBreaker cd = new BlockBreaker(world, blockID, x, y, z, BBSettings.drops == 2);
                        
                        cd.harvestConnectedBlocks(x, y, z);
                    }
                }
                else
                {
                    if (itemCanHarvestBlock && BlockBreakerMod.instance.isBlockInGroups(new int[] { blockID.id, metadata }, BBSettings.blockGroups))
                    {
                        if (BlockBreakerMod.instance.isMetadataNull(blockID.id, BBSettings.blockGroups))
                            blockID = new BlockID(blockID.id);
                        
                        BlockBreaker cd = new BlockBreaker(world, blockID, x, y, z, BBSettings.drops > 0);
                        
                        cd.harvestConnectedBlocks(x, y, z);
                    }
                }
                
            }
        }
    }
    
    public boolean isIDInList(String list, int id, int metadata)
    {
        String[] itemArray = list.split(";");
        for (int i = 0; i < itemArray.length; i++)
        {
            String[] values = itemArray[i].split(",");
            int tempID = parseInt(values[0]);
            if (values.length > 1 && parseInt(values[1]) == metadata)
                return true;
            else if (tempID == id)
                return true;
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
        catch (NumberFormatException e)
        {
            ModLoader.getLogger().log(Level.WARNING, "Invalid number format in string " + string);
        }
        return res;
    }
    
    public int[][][] stringToGroups(String string)
    {
        List<int[][]> groupList = new ArrayList<int[][]>();
        String[] groups = string.split(";");
        for (String group : groups)
        {
            groupList.add(stringToGroup(group));
        }
        int[][][] res = new int[groupList.size()][][];
        for (int i = 0; i < groupList.size(); i++)
        {
            res[i] = groupList.get(i);
        }
        return res;
    }
    
    public int[][] stringToGroup(String string)
    {
        List<int[]> blockList = new ArrayList<int[]>();
        String[] blocks = string.split(">");
        for (String block : blocks)
        {
            blockList.add(stringToBlock(block));
        }
        int[][] res = new int[blockList.size()][];
        for (int i = 0; i < blockList.size(); i++)
        {
            res[i] = blockList.get(i);
        }
        return res;
    }
    
    public int[] stringToBlock(String string)
    {
        int[] values = new int[] { 0, -1 };
        String[] src = string.split(",");
        if (src.length < 1)
            return values;
        values[0] = parseInt(src[0]);
        if (src.length < 2)
            return values;
        values[1] = parseInt(src[1]);
        return values;
    }
    
    public boolean isBlockInGroups(int[] block, int[][][] groups)
    {
        for (int[][] group : groups)
        {
            if (indexOfBlock(block, group) > -1)
                return true;
        }
        return false;
    }
    
    public int indexOfBlock(int[] block, int[][] group)
    {
        for (int i = 0; i < group.length; i++)
        {
            if (block[0] == group[i][0])
            {
                if (group[i][1] == -1 || block[1] == group[i][1])
                    return i;
            }
        }
        return -1;
    }
    
    public int[][] getRelatedBlocks(int[] block, int[][][] groups)
    {
        List<int[]> blockList = new ArrayList<int[]>();
        for (int[][] group : groups)
        {
            if (indexOfBlock(block, group) > -1)
            {
                for (int i = 0; i < groups.length; i++)
                {
                    if (blockList.contains(group[i]))
                        continue;
                    blockList.add(i, group[i]);
                }
            }
        }
        int[][] secondary = new int[blockList.size()][];
        for (int i = 0; i < blockList.size(); i++)
        {
            secondary[i] = blockList.get(i);
        }
        return secondary;
    }
    
    public int smallerBlockIndex(int[] block, int[][][] groups)
    {
        int min = Integer.MAX_VALUE;
        for (int[][] group : groups)
        {
            int i = indexOfBlock(block, group);
            if (i > -1 && i < min)
                min = i;
        }
        if (min == Integer.MAX_VALUE)
            min = -1;
        return min;
    }
    
    public boolean isMetadataNull(int id, int[][][] groups)
    {
        for (int[][] group : groups)
        {
            for (int[] block : group)
            {
                if (block[0] == id && block[1] == -1)
                    return true;
            }
        }
        return false;
    }
}
