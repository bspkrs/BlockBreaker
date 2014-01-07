package bspkrs.blockbreaker;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import bspkrs.fml.util.TickerBase;
import bspkrs.util.BlockID;
import bspkrs.util.CommonUtils;
import bspkrs.util.Coord;
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
    private Coord           dropPos;
    private BlockID         blockID;
    private World           world;
    private EntityPlayer    player;
    private List<ItemStack> drops;
    private ItemStack       tool;
    
    public BlockBreaker(World world, EntityPlayer player, BlockID blockData, int x, int y, int z, boolean enableDrops)
    {
        this.world = world;
        this.player = player;
        TickRegistry.registerTickHandler(new BBTicker().addTicks(EnumSet.of(TickType.SERVER)), Side.SERVER);
        this.enableDrops = enableDrops;
        blocksHarvested = 0;
        scheduledBlocks = new ArrayList<Coord>();
        this.blockID = blockData;
        startingPos = new Coord(x, y, z);
        dropPos = startingPos.clone();
        drops = new ArrayList<ItemStack>();
        tool = player.getCurrentEquippedItem();
    }
    
    @SuppressWarnings("unused")
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
     * Breaks all the connected blocks of the same type within a specific range of the starting block
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
                    int id = blockPos.getBlockID(world);
                    
                    Block block = Block.blocksList[id];
                    if (block == null)
                        continue;
                    
                    // dirty fix for redstone ore
                    if ((id == 73 || id == 74) && (blockID.id == 73 || blockID.id == 74))
                        id = blockID.id;
                    
                    int metadata = blockPos.getBlockMetadata(world);
                    if (id == blockID.id && (blockID.metadata == -1 || blockID.metadata == metadata))
                    {
                        int distance = BBSettings.breakShape.equalsIgnoreCase("cubic") ? getDistance(blockPos.x, blockPos.y, blockPos.z)
                                : getSphericalDistance(blockPos.x, blockPos.y, blockPos.z);
                        if ((LIMIT == -1 || blocksHarvested <= LIMIT) && (MAX_DISTANCE == -1 || distance <= MAX_DISTANCE))
                        {
                            if (enableDrops)
                            {
                                addDrop(block, metadata, blockPos);
                                if (tool != null && BBSettings.allowItemDamage)
                                {
                                    tool.getItem().onBlockDestroyed(tool, world, id, blockPos.x, blockPos.y, blockPos.z, player);
                                    if (tool != null && tool.stackSize < 1)
                                    {
                                        player.destroyCurrentEquippedItem();
                                        tool = null;
                                        scheduledBlocks.clear();
                                    }
                                }
                            }
                            
                            blocksHarvested++;
                            
                            if (world.blockHasTileEntity(blockPos.x, blockPos.y, blockPos.z))
                                world.removeBlockTileEntity(blockPos.x, blockPos.y, blockPos.z);
                            world.setBlock(blockPos.x, blockPos.y, blockPos.z, 0, 0, 3);
                            
                            if ((BBSettings.allowItemDamage && tool != null) || !BBSettings.allowItemDamage)
                            {
                                if (!scheduledBlocks.contains(blockPos))
                                    scheduledBlocks.add(blockPos);
                            }
                            else
                                return;
                        }
                    }
                }
            }
        }
    }
    
    private void addDrop(Block block, int metadata, Coord pos)
    {
        player.addStat(StatList.mineBlockStatArray[block.blockID], 1);
        player.addExhaustion(0.025F);
        List<ItemStack> stacks = null;
        
        dropPos = BBSettings.itemsDropInPlace ? pos.clone() : startingPos.clone();
        
        if (block.canSilkHarvest(world, player, pos.x, pos.y, pos.z, metadata) && EnchantmentHelper.getSilkTouchModifier(player))
        {
            stacks = new ArrayList<ItemStack>();
            stacks.add(new ItemStack(block.blockID, 1, metadata));
        }
        else
        {
            int fortune = EnchantmentHelper.getFortuneModifier(player);
            stacks = block.getBlockDropped(world, pos.x, pos.y, pos.z, metadata, fortune);
            // for dropXp
            block.dropBlockAsItemWithChance(world, dropPos.x, dropPos.y, dropPos.z, metadata, -1.0F, fortune);
        }
        
        if (stacks == null)
            return;
        for (ItemStack drop : stacks)
        {
            if (drop == null)
                continue;
            
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
                int quantity = drop.stackSize;
                drop = drops.get(index);
                drop.stackSize += quantity;
            }
            
            if (drop.stackSize >= drop.getMaxStackSize())
            {
                int i = drop.stackSize - drop.getMaxStackSize();
                drop.stackSize = drop.getMaxStackSize();
                world.spawnEntityInWorld(new EntityItem(world, dropPos.x, dropPos.y, dropPos.z, drop));
                if (i > 0)
                    drop.stackSize = i;
                else
                    drops.remove(index);
            }
        }
    }
    
    @SuppressWarnings("unused")
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
            dropPos = BBSettings.itemsDropInPlace ? blockPos.clone() : startingPos.clone();
            harvestConnectedBlocks(blockPos.x, blockPos.y, blockPos.z);
        }
        
        if (scheduledBlocks.size() == 0)
        {
            while (drops.size() > 0)
                world.spawnEntityInWorld(new EntityItem(world, dropPos.x, dropPos.y, dropPos.z, drops.remove(0)));
        }
        
        return scheduledBlocks;
    }
    
    private class BBTicker extends TickerBase
    {
        @Override
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
        public String getLabel()
        {
            return "BlockBreaker";
        }
    }
}
