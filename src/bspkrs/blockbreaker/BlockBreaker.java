package bspkrs.blockbreaker;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import bspkrs.util.BlockID;
import bspkrs.util.CommonUtils;
import bspkrs.util.Coord;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class BlockBreaker
{
    
    private final int       LIMIT           = BBSettings.blockLimit;
    private final int       MAX_DISTANCE    = BBSettings.maxDistance;
    private final int       BLOCKS_PER_TICK = BBSettings.blocksPerTick;
    public int              blocksHarvested;
    private boolean         enableDrops;
    private List<Coord>     scheduledBlocks = new ArrayList<Coord>();
    
    private Coord           startingPos;
    private BlockID         blockID;
    private World           world;
    private List<ItemStack> drops;
    
    public BlockBreaker(World world, BlockID blockData, int x, int y, int z, boolean enableDrops)
    {
        this.world = world;
        TickRegistry.registerTickHandler(new BBTicker(EnumSet.of(TickType.WORLD)), Side.SERVER);
        this.enableDrops = enableDrops;
        blocksHarvested = 0;
        scheduledBlocks = new ArrayList<Coord>();
        this.blockID = blockData;
        startingPos = new Coord(x, y, z);
        drops = new ArrayList<ItemStack>();
    }
    
    private String iterate(int[][] group)
    {
        String res = "";
        for (int[] block : group)
        {
            res = res.concat(Integer.toString(block[0])).concat(", ").concat(Integer.toString(block[1])).concat("; ");
        }
        return res;
    }
    
    /*
     * Breaks all the
     */
    public void harvestConnectedBlocks(int x, int y, int z)
    {
        byte d = 1;
        for (int dx = -d; dx <= d; dx++)
        {
            for (int dy = -d; dy <= d; dy++)
            {
                for (int dz = -d; dz <= d; dz++)
                {
                    if (dx == 0 && dy == 0 && dz == 0)
                        continue;
                    
                    Coord blockPos = new Coord(x + dx, y + dy, z + dz);
                    int id = world.getBlockId(blockPos.x, blockPos.y, blockPos.z);
                    
                    Block block = Block.blocksList[id];
                    if (block == null)
                        continue;
                    
                    int metadata = world.getBlockMetadata(blockPos.x, blockPos.y, blockPos.z);
                    if (id == blockID.id && (blockID.metadata == -1 || blockID.metadata == metadata))
                    {
                        if ((LIMIT == -1 || blocksHarvested <= LIMIT) && (MAX_DISTANCE == -1
                                || getSphericalDistance(blockPos.x, blockPos.y, blockPos.z) <= MAX_DISTANCE))
                        {
                            if (world.blockHasTileEntity(blockPos.x, blockPos.y, blockPos.z))
                                world.removeBlockTileEntity(blockPos.x, blockPos.y, blockPos.z);
                            world.setBlockWithNotify(blockPos.x, blockPos.y, blockPos.z, 0);
                            
                            if (enableDrops)
                                addDrop(block, metadata);
                            
                            blocksHarvested++;
                            
                            if (!scheduledBlocks.contains(blockPos))
                                scheduledBlocks.add(blockPos);
                        }
                    }
                }
            }
        }
    }
    
    private void addDrop(Block block, int metadata)
    {
        Random random = new Random();
        int idDropped = block.idDropped(0, random, 0);
        int damage = block.damageDropped(metadata);
        int quantity = block.quantityDropped(random);
        ItemStack drop = new ItemStack(idDropped, quantity, damage);
        
        int index = -1;
        for (int i = 0; i < drops.size(); i++)
        {
            if (drops.get(i).isItemEqual(drop))
            {
                index = i;
                break;
            }
        }
        
        if (index == -1)
        {
            drops.add(drop);
            index = drops.indexOf(drop);
        }
        else
        {
            drop = drops.get(index);
            drop.stackSize += quantity;
        }
        
        if (drop.stackSize >= drop.getMaxStackSize())
        {
            int i = drop.stackSize - drop.getMaxStackSize();
            drop.stackSize = drop.getMaxStackSize();
            world.spawnEntityInWorld(new EntityItem(world, startingPos.x, startingPos.y, startingPos.z, drop));
            if (i > 0)
                drop.stackSize = i;
            else
                drops.remove(index);
        }
    }
    
    private int getCubicDistance(int x, int y, int z)
    {
        return Math.max(Math.abs(x - startingPos.x), Math.max(Math.abs(y - startingPos.y), Math.abs(z - startingPos.z)));
    }
    
    private int getSphericalDistance(int x, int y, int z)
    {
        return (int) Math.round(Math.sqrt(CommonUtils.sqr(x - startingPos.x) + CommonUtils.sqr(z - startingPos.z) + CommonUtils.sqr(y - startingPos.y)));
    }
    
    private int getDistance(int x, int y, int z)
    {
        return Math.abs(x - startingPos.x) + Math.abs(y - startingPos.y) + Math.abs(z - startingPos.z);
    }
    
    private List<Coord> destroyScheduledBlocks(List<Coord> scheduledBlocks)
    {
        int removed = 0;
        while (scheduledBlocks.size() > 0 && removed < BLOCKS_PER_TICK)
        {
            Coord blockPos = scheduledBlocks.remove(0);
            removed++;
            harvestConnectedBlocks(blockPos.x, blockPos.y, blockPos.z);
        }
        
        if (scheduledBlocks.size() == 0)
        {
            while (drops.size() > 0)
                world.spawnEntityInWorld(new EntityItem(world, startingPos.x, startingPos.y, startingPos.z, drops.remove(0)));
        }
        
        return scheduledBlocks;
    }
    
    private class BBTicker implements ITickHandler
    {
        private EnumSet<TickType> tickTypes = EnumSet.noneOf(TickType.class);
        
        public BBTicker(EnumSet<TickType> tickTypes)
        {
            this.tickTypes = tickTypes;
        }
        
        @Override
        public void tickStart(EnumSet<TickType> tickTypes, Object... tickData)
        {
            tick(tickTypes, true);
        }
        
        @Override
        public void tickEnd(EnumSet<TickType> tickTypes, Object... tickData)
        {
            tick(tickTypes, false);
        }
        
        private void tick(EnumSet<TickType> tickTypes, boolean isStart)
        {
            for (TickType tickType : tickTypes)
            {
                if (!onTick(tickType, isStart))
                {
                    this.tickTypes.remove(tickType);
                    this.tickTypes.removeAll(tickType.partnerTicks());
                }
            }
        }
        
        public boolean onTick(TickType tick, boolean isStart)
        {
            if (isStart)
            {
                return true;
            }
            
            if (scheduledBlocks.size() > 0)
            {
                destroyScheduledBlocks(scheduledBlocks);
            }
            return scheduledBlocks.size() > 0;
        }
        
        @Override
        public EnumSet<TickType> ticks()
        {
            return this.tickTypes;
        }
        
        @Override
        public String getLabel()
        {
            return "BlockBreaker";
        }
    }
}
