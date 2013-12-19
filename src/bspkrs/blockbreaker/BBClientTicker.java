package bspkrs.blockbreaker;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import bspkrs.bspkrscore.fml.bspkrsCoreMod;
import bspkrs.fml.util.TickerBase;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BBClientTicker extends TickerBase
{
    private Minecraft mcClient;
    
    public BBClientTicker(EnumSet<TickType> tickTypes)
    {
        super(tickTypes);
        this.mcClient = FMLClientHandler.instance().getClient();
    }
    
    @Override
    public boolean onTick(TickType tick, boolean isStart)
    {
        if (isStart)
        {
            return true;
        }
        
        if (mcClient != null && mcClient.thePlayer != null)
        {
            if (bspkrsCoreMod.instance.allowUpdateCheck && BlockBreakerMod.versionChecker != null)
                if (!BlockBreakerMod.versionChecker.isCurrentVersion())
                    for (String msg : BlockBreakerMod.versionChecker.getInGameMessage())
                        mcClient.thePlayer.addChatMessage(msg);
            
            return false;
        }
        
        return true;
    }
    
    @Override
    public String getLabel()
    {
        return "TreeCapitatorTicker";
    }
    
}
