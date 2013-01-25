package bspkrs.connecteddestruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;

public class ConnectedDestruction 
{

    private final int LIMIT = ConnectedDestructionMod.blockLimit;
    private final int MAX_DISTANCE = ConnectedDestructionMod.maximumDistance;
    private final int BLOCKS_PER_TICK = ConnectedDestructionMod.blocksPerTick;
    public int blocksHarvested;
    private boolean enableDrops;
    private List scheduledBlocks = new ArrayList();
    
    private int initial_x, initial_y, initial_z;
    private int[] blockData;
    private World world;
    private ArrayList<ItemStack> drops;
    
    public ConnectedDestruction(World world, int[] blockData, int x, int y, int z, boolean enableDrops)
    {
        this.world = world;
        ModLoader.setInGameHook(this.new TickHelper(), true, true);
        this.enableDrops = enableDrops;
        blocksHarvested = 0;
        scheduledBlocks = new ArrayList();
        this.blockData = blockData;
        initial_x = x;
        initial_y = y;
        initial_z = z;
        drops = new ArrayList<ItemStack>();
    }
    
    private String iterate(int[][] group)
    {
        String res = "";
        for(int[] block: group)
        {
            res = res.concat(Integer.toString(block[0])).concat(", ").concat(Integer.toString(block[1])).concat("; ");
        }
        return res;
    }
    
    public void harvestConnectedBlocks(int x, int y, int z) 
    {
        byte d = 1;
        for(int dx = -d; dx <= d; dx++)
        {
            for(int dy = -d; dy <= d; dy++)
            {
                for(int dz = -d; dz <= d; dz++)
                {
                    if(dx == 0 && dy == 0 && dz == 0) continue;
                    int[] blockPos = new int[]{x + dx, y + dy, z + dz};
                    int blockID = world.getBlockId(blockPos[0], blockPos[1], blockPos[2]);
                    int blockMetadata = world.getBlockMetadata(blockPos[0], blockPos[1], blockPos[2]);
                    if(blockID == blockData[0] && (blockData[1] == -1 || blockData[1] == blockMetadata))
                    {
                        if((LIMIT == -1 || blocksHarvested <= LIMIT) && (MAX_DISTANCE == -1 || getCubicDistance(blockPos[0], blockPos[1], blockPos[2]) <= MAX_DISTANCE))
                        {
                            Block block = Block.blocksList[blockID];
                            if(block == null) continue;
                            //world.setBlockWithNotify(blockPos[0], blockPos[1], blockPos[2], 0);
                            world.setBlock(blockPos[0], blockPos[1], blockPos[2], 0);
                            if(enableDrops) addDrop(block, blockMetadata); //block.dropBlockAsItemWithChance(world, initial_x, initial_y, initial_z, blockMetadata, 1.0F, 0);
                            blocksHarvested++;
                            
                            if(!scheduledBlocks.contains(blockPos))
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
        for(int i = 0; i < drops.size(); i++)
        {
        	if(drops.get(i).isItemEqual(drop))
        	{
        		index = i;
        		break;
        	}
        }
        
        if(index == -1)
        {
            drops.add(drop);
            index = drops.indexOf(drop);
        }
        else
        {
        	drop = drops.get(index);
            drop.stackSize += quantity;
        }
        
        if(drop.stackSize >= drop.getMaxStackSize())
        {
            int i = drop.stackSize - drop.getMaxStackSize();
            drop.stackSize = drop.getMaxStackSize();
            world.spawnEntityInWorld(new EntityItem(world, initial_x, initial_y, initial_z, drop));
            if(i > 0)
                drop.stackSize = i;
            else
                drops.remove(index);
        }
    }
    
    private int getCubicDistance(int x, int y, int z)
    {
        return Math.max(Math.abs(x - initial_x), Math.max(Math.abs(y - initial_y), Math.abs(z - initial_z)));
    }
    
    private int getDistance(int x, int y, int z) 
    {
        return Math.abs(x - initial_x) + Math.abs(y - initial_y) + Math.abs(z - initial_z);
    }

    private List<Integer[]> destroyScheduledBlocks(List scheduledBlocks)
    {
        int removed = 0;
        while(scheduledBlocks.size() > 0 && removed < BLOCKS_PER_TICK)
        {
            int[] blockPos = (int[])scheduledBlocks.remove(0);
            removed++;
            int[] block = new int[]{
                    world.getBlockId(blockPos[0], blockPos[1], blockPos[2]),
                    world.getBlockMetadata(blockPos[0], blockPos[1], blockPos[2])
            };
            harvestConnectedBlocks(blockPos[0], blockPos[1], blockPos[2]);
        }
        
        if(scheduledBlocks.size() == 0)
        {
            while(drops.size() > 0)
            	world.spawnEntityInWorld(new EntityItem(world, initial_x, initial_y, initial_z, drops.remove(0)));
        }
            
        return scheduledBlocks;
    }
    
    private class TickHelper extends BaseMod
    {
        @Override
        public String getVersion() 
        {
            return null;
        }

        @Override
        public void load() 
        {
            
        }
        
        @Override
        public boolean onTickInGame(float f, Minecraft minecraft)
        {
            if(scheduledBlocks.size() > 0)
            {
                destroyScheduledBlocks(scheduledBlocks);
            }
            return scheduledBlocks.size() > 0;
        }
    }
}
