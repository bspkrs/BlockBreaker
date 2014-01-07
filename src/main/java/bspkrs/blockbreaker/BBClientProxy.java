package bspkrs.blockbreaker;

import java.util.EnumSet;

import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BBClientProxy extends BBCommonProxy
{
    @Override
    public void onLoad()
    {
        new BBClient();
        TickRegistry.registerTickHandler(new BBClientTicker(EnumSet.of(TickType.CLIENT)), Side.CLIENT);
    }
    
    @Override
    public boolean isEnabled()
    {
        return BBClient.instance.serverDetected;
    }
}
