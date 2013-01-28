package bspkrs.blockbreaker;

import bspkrs.fml.util.ForgePacketHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;

public class BlockBreakerClient
{
    public static BlockBreakerClient instance;
    public boolean                   serverDetected;
    
    public BlockBreakerClient()
    {
        instance = this;
        serverDetected = false;
    }
    
    public void onClientConnect()
    {
        serverDetected = false;
        PacketDispatcher.sendPacketToServer(ForgePacketHelper.createPacket("BlockBreaker", 0, null));
    }
    
    public void setServerDetected()
    {
        serverDetected = BlockBreakerMod.instance.isCoreModLoaded;
        if (serverDetected)
            FMLClientHandler.instance().getClient().thePlayer.addChatMessage("BlockBreaker client-side features enabled.");
        else
        {
            String s = "BlockBreaker CoreMod code has not been injected. Ensure the downloaded .jar file is in the coremods folder and not mods.";
            FMLClientHandler.instance().getClient().thePlayer.addChatMessage(s);
            BBLog.severe(s);
        }
    }
    
    public void onServerConfigReceived(String blockIDList, String axeIDList, float logHardnessNormal, float logHardnessModified)
    {   
        
    }
}