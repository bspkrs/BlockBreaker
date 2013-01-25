package bspkrs.connecteddestruction;

import java.io.IOException;
import java.net.UnknownHostException;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.PlayerControllerMP;
import net.minecraft.src.World;

public class ConnectedDestructionMP extends PlayerControllerMP 
{
    private Minecraft mc;
    
    public ConnectedDestructionMP(Minecraft minecraft, NetClientHandler netClientHandler)
    {
        super(minecraft, netClientHandler);
        mc = minecraft;
    }

    /**
     * Called by Minecraft class when the player is hitting a block with an item. Args: x, y, z, side
     */
//    @Override
//    public void clickBlock(int par1, int par2, int par3, int par4)
//    {
//        Block block = Block.blocksList[ModLoader.getMinecraftInstance().theWorld.getBlockId(par1, par2, par3)];
//        if (block != null)
//        {
//            int metadata = ModLoader.getMinecraftInstance().theWorld.getBlockMetadata(par1, par2, par3);
//            ModLoader.getMinecraftInstance().thePlayer.addChatMessage("" + block.blockID + ", " + metadata);
//        }
//        super.clickBlock(par1, par2, par3, par4);
//    }

    @Override
    public boolean onPlayerDestroyBlock(int i, int j, int k, int l)
    {
        World world = mc.getIntegratedServer().worldServerForDimension(mc.thePlayer.dimension);
        int id = world.getBlockId(i, j, k);
        int md = world.getBlockMetadata(i, j, k);
        boolean flag = super.onPlayerDestroyBlock(i, j, k, l);
        
        Block block = Block.blocksList[id];
        ItemStack item = mc.thePlayer.getCurrentEquippedItem();
        boolean itemCanHarvestBlock = (item != null) && (item.canHarvestBlock(block));
        
        if(ModLoader.getMinecraftInstance().isSingleplayer())
        {
            /*if(this.isInCreativeMode())
            {
                if(flag && ConnectedDestructionMod.instance.isBlockInGroups(new int[]{id, md}, ConnectedDestructionMod.instance.blockGroups))
                {
                    if(ConnectedDestructionMod.instance.isMetadataNull(id, ConnectedDestructionMod.instance.blockGroups)) 
                        md = -1;
                    ConnectedDestruction cd = new ConnectedDestruction(world, new int[]{id, md}, i, j, k, ConnectedDestructionMod.drops == 2);
                    cd.harvestConnectedBlocks(i, j, k);
                }
            }
            else 
            {
                if(flag && ConnectedDestructionMod.instance.isBlockInGroups(new int[]{id, md}, ConnectedDestructionMod.instance.blockGroups))
                {
                    if(ConnectedDestructionMod.instance.isMetadataNull(id, ConnectedDestructionMod.instance.blockGroups)) 
                        md = -1;
                    ConnectedDestruction cd = new ConnectedDestruction(world, new int[]{id, md}, i, j, k, ConnectedDestructionMod.drops > 0);
                    cd.harvestConnectedBlocks(i, j, k);
                }                
            }*/
        }
        else
        {
//            Packet230ModLoader packet = new Packet230ModLoader();
//            packet.dataInt = new int[]{i, j, k, l, id, md};
//            packet.dataString = new String[]{"ConDes"};
//            ModLoaderMp.sendPacket(mod_connectedDestruction.instance, packet);
        }
        return flag;
    }
}
