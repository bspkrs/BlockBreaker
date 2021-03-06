package bspkrs.blockbreaker;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import bspkrs.bspkrscore.fml.bspkrsCoreMod;
import bspkrs.util.BlockID;
import bspkrs.util.CommonUtils;
import bspkrs.util.Const;
import bspkrs.util.ModVersionChecker;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;

@Mod(name = "BlockBreaker", modid = "BlockBreaker", version = "Forge " + Strings.MOD_VERSION_NUMBER, dependencies = "required-after:bspkrsCore", useMetadata = true)
@NetworkMod(clientSideRequired = false, serverSideRequired = false,
        clientPacketHandlerSpec = @SidedPacketHandler(channels = { "BlockBreaker" }, packetHandler = BBClientPacketHandler.class),
        serverPacketHandlerSpec = @SidedPacketHandler(channels = { "BlockBreaker" }, packetHandler = BBServerPacketHandler.class),
        connectionHandler = BBConnectionHandler.class)
public class BlockBreakerMod
{
    public static ModVersionChecker versionChecker;
    private String                  versionURL = Const.VERSION_URL + "/Minecraft/" + Const.MCVERSION + "/blockBreakerForge.version";
    private String                  mcfTopic   = "http://www.minecraftforum.net/topic/1009577-";
    
    @Metadata(value = "BlockBreaker")
    public static ModMetadata       metadata;
    
    @SidedProxy(clientSide = "bspkrs.blockbreaker.BBClientProxy", serverSide = "bspkrs.blockbreaker.BBCommonProxy")
    public static BBCommonProxy     proxy;
    
    @Instance(value = "BlockBreaker")
    public static BlockBreakerMod   instance;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        metadata = event.getModMetadata();
        BBSettings.loadConfig(event.getSuggestedConfigurationFile());
        
        if (bspkrsCoreMod.instance.allowUpdateCheck)
        {
            versionChecker = new ModVersionChecker(metadata.name, metadata.version, versionURL, mcfTopic);
            versionChecker.checkVersionWithLogging();
        }
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
        proxy.onLoad();
    }
    
    @EventHandler
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
            
            boolean canHarvestBlock = ForgeHooks.canHarvestBlock(block, player, metadata);
            if (!world.isRemote && CommonUtils.isBlockInGroups(new int[] { blockID.id, metadata }, BBSettings.blockGroups) && isBreakingEnabled(player))
            {
                if (CommonUtils.isMetadataNull(blockID.id, BBSettings.blockGroups))
                    blockID = new BlockID(blockID.id);
                
                if (player.capabilities.isCreativeMode)
                {
                    BlockBreaker cd = new BlockBreaker(world, player, blockID, x, y, z, BBSettings.itemDropMode >= 2);
                    cd.harvestConnectedBlocks(x, y, z);
                }
                else if (world.getWorldInfo().getGameType().equals(EnumGameType.ADVENTURE))
                {
                    if (canHarvestBlock)
                    {
                        BlockBreaker cd = new BlockBreaker(world, player, blockID, x, y, z, BBSettings.itemDropMode == 3);
                        cd.harvestConnectedBlocks(x, y, z);
                    }
                }
                else
                // Survival
                {
                    if (canHarvestBlock)
                    {
                        BlockBreaker cd = new BlockBreaker(world, player, blockID, x, y, z, BBSettings.itemDropMode >= 1);
                        cd.harvestConnectedBlocks(x, y, z);
                    }
                }
            }
        }
    }
}
