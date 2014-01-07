package bspkrs.blockbreaker;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class ForgeEventHandler
{
    @ForgeSubscribe
    public void onBlockHarvested(BreakEvent event)
    {
        if (event.block != null && event.world != null && event.getPlayer() != null)
            BlockBreakerMod.instance.onBlockHarvested(event.world, event.x, event.y, event.z, event.block, event.blockMetadata, event.getPlayer());
    }
}
