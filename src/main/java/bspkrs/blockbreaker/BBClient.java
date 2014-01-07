package bspkrs.blockbreaker;

import bspkrs.fml.util.ForgePacketHelper;
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
        BBLog.info("BlockBreaker server detected.");
    }
}
