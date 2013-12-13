package bspkrs.blockbreaker;

import bspkrs.fml.util.ForgePacketHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BBClient
{
    public static BBClient instance;
    public boolean         serverDetected;
    
    public BBClient()
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
        serverDetected = true;
        
        if (!BlockBreakerMod.isCoreModLoaded && FMLClientHandler.instance().getClient().isSingleplayer())
        {
            FMLClientHandler.instance().getClient().thePlayer.addChatMessage("BlockBreaker hook has not been injected. Possible causes: 1. You deleted META-INF from the mod archive. Don't do this. 2. You are trying to run from Eclipse and forgot to put the dummy jar in the mcp/jars/mods folder.");
            serverDetected = false;
        }
        else
            BBLog.info("BlockBreaker server detected.");
    }
}
