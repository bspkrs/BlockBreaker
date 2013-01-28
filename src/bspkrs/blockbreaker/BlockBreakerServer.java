package bspkrs.blockbreaker;

import bspkrs.fml.util.ForgePacketHelper;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class BlockBreakerServer
{
    public static BlockBreakerServer instance;
    
    public BlockBreakerServer()
    {
        instance = this;
    }
    
    public void onPlayerLoggedIn(Player player)
    {
        Object[] toSend = { "", "", 2.0F, 4.0F };
        PacketDispatcher.sendPacketToPlayer(ForgePacketHelper.createPacket("BlockBreaker", 1, toSend), player);
    }
}
