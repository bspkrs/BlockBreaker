package bspkrs.blockbreaker;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import bspkrs.fml.util.ForgePacketHelper;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BBClientPacketHandler implements IPacketHandler
{
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
        int packetType = ForgePacketHelper.readPacketID(data);
        
        if (packetType == 0)
        {
            BBClient.instance.setServerDetected();
        }
    }
}
