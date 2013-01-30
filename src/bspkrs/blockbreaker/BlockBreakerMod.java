package bspkrs.blockbreaker;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeHooks;
import bspkrs.util.BlockID;
import bspkrs.util.CommonUtils;
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
        clientPacketHandlerSpec = @SidedPacketHandler(channels = { "BlockBreaker" }, packetHandler = BBClientPacketHandler.class),
        serverPacketHandlerSpec = @SidedPacketHandler(channels = { "BlockBreaker" }, packetHandler = BBServerPacketHandler.class),
        connectionHandler = BBConnectionHandler.class)
public class BlockBreakerMod
{
    public static ModVersionChecker versionChecker;
    private String                  versionURL      = "https://dl.dropbox.com/u/20748481/Minecraft/1.4.6/blockBreakerForge.version";
    private String                  mcfTopic        = "http://www.minecraftforum.net/topic/1009577-";
    
    public ModMetadata              metadata;
    
    // Gets set in the Class Transformer
    public static boolean           isCoreModLoaded = false;
    
    @SidedProxy(clientSide = "bspkrs.blockbreaker.BBClientProxy", serverSide = "bspkrs.blockbreaker.BBCommonProxy")
    public static BBCommonProxy     proxy;
    
    @Instance(value = "BlockBreaker")
    public static BlockBreakerMod   instance;
    
    private static Loader           loader;
    
    public BlockBreakerMod()
    {
        loader = Loader.instance();
    }
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        metadata = event.getModMetadata();
        BBSettings.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));
        
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
        new BBServer();
    }
    
    public static boolean isBreakingEnabled(EntityPlayer player)
    {
        return (BBSettings.sneakAction.equalsIgnoreCase("none")
                || (BBSettings.sneakAction.equalsIgnoreCase("disable") && !player.isSneaking())
                || (BBSettings.sneakAction.equalsIgnoreCase("enable") && player.isSneaking()));
    }
    
    // This method is invoked from removeBlock() in ItemInWorldManager
    public void onBlockHarvested(World world, int x, int y, int z, Block block, int metadata, EntityPlayer player)
    {
        if (proxy.isEnabled())
        {
            BlockID blockID = new BlockID(block, metadata);
            
            ItemStack item = player.getCurrentEquippedItem();
            boolean canHarvestBlock = ForgeHooks.canHarvestBlock(block, player, metadata) && isBreakingEnabled(player);
            if (!world.isRemote)
            {
                if (player.capabilities.isCreativeMode)
                {
                    if (canHarvestBlock && CommonUtils.isBlockInGroups(new int[] { blockID.id, metadata }, BBSettings.blockGroups))
                    {
                        if (CommonUtils.isMetadataNull(blockID.id, BBSettings.blockGroups))
                            blockID = new BlockID(blockID.id);
                        
                        BlockBreaker cd = new BlockBreaker(world, blockID, x, y, z, BBSettings.itemDropMode == 2);
                        cd.harvestConnectedBlocks(x, y, z);
                    }
                }
                else
                {
                    if (canHarvestBlock && CommonUtils.isBlockInGroups(new int[] { blockID.id, metadata }, BBSettings.blockGroups))
                    {
                        if (CommonUtils.isMetadataNull(blockID.id, BBSettings.blockGroups))
                            blockID = new BlockID(blockID.id);
                        
                        BlockBreaker cd = new BlockBreaker(world, blockID, x, y, z, BBSettings.itemDropMode > 0);
                        cd.harvestConnectedBlocks(x, y, z);
                    }
                }
            }
        }
    }
    
    public static boolean isItemInWorldManagerReplaced(EntityPlayerMP player)
    {
        return !player.theItemInWorldManager.getClass().getSimpleName().equals(ItemInWorldManager.class.getSimpleName());
    }
}
