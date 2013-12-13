package bspkrs.blockbreaker.asm;

/*
 * Invaluable help from AtomicStryker's MultiMine coremod code <3
 */

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;

import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import bspkrs.blockbreaker.BBLog;
import bspkrs.blockbreaker.BlockBreakerMod;

public class ItemInWorldManagerTransformer implements net.minecraft.launchwrapper.IClassTransformer
{
    /* Obfuscated Names for ItemInWorldManager Transformation */
    
    /* removeBlock */
    private final String                  targetMethodDesc = "(III)Z";
    
    private final HashMap<String, String> obfStrings;
    private final HashMap<String, String> mcpStrings;
    
    public ItemInWorldManagerTransformer()
    {
        /*
         * create a HashMap to store the obfuscated names of classes, methods, and fields used in the transformation
         */
        obfStrings = new HashMap<String, String>();
        
        /* 1.6.4 mappings */
        /* net.minecraft.src.ItemInWorldManager */
        obfStrings.put("className", "jw");
        /* net/minecraft/src/ItemInWorldManager */
        obfStrings.put("javaClassName", "jw");
        /* removeBlock */
        obfStrings.put("targetMethodName", "d");
        /* theWorld */
        obfStrings.put("worldFieldName", "a");
        /* thisPlayerMP */
        obfStrings.put("entityPlayerFieldName", "b");
        /* net/minecraft/src/World */
        obfStrings.put("worldJavaClassName", "abw");
        /* net/minecraft/src/World.getBlockMetadata() */
        obfStrings.put("getBlockMetadataMethodName", "h");
        /* net/minecraft/src/Block */
        obfStrings.put("blockJavaClassName", "aqz");
        /* net/minecraft/src/Block.blocksList[] */
        obfStrings.put("blocksListFieldName", "s");
        /* net/minecraft/src/EntityPlayer */
        obfStrings.put("entityPlayerJavaClassName", "uf");
        /* net/minecraft/src/EntityPlayerMP */
        obfStrings.put("entityPlayerMPJavaClassName", "jv");
        
        /* 1.6.2 mappings */
        //        /* net.minecraft.src.ItemInWorldManager */
        //        obfStrings.put("className", "jv");
        //        /* net/minecraft/src/ItemInWorldManager */
        //        obfStrings.put("javaClassName", "jv");
        //        /* removeBlock */
        //        obfStrings.put("targetMethodName", "d");
        //        /* theWorld */
        //        obfStrings.put("worldFieldName", "a");
        //        /* thisPlayerMP */
        //        obfStrings.put("entityPlayerFieldName", "b");
        //        /* net/minecraft/src/World */
        //        obfStrings.put("worldJavaClassName", "abv");
        //        /* net/minecraft/src/World.getBlockMetadata() */
        //        obfStrings.put("getBlockMetadataMethodName", "h");
        //        /* net/minecraft/src/Block */
        //        obfStrings.put("blockJavaClassName", "aqw");
        //        /* net/minecraft/src/Block.blocksList[] */
        //        obfStrings.put("blocksListFieldName", "s");
        //        /* net/minecraft/src/EntityPlayer */
        //        obfStrings.put("entityPlayerJavaClassName", "ue");
        //        /* net/minecraft/src/EntityPlayerMP */
        //        obfStrings.put("entityPlayerMPJavaClassName", "ju");
        
        /* 1.6.1 mappings */
        //        /* net.minecraft.src.ItemInWorldManager */
        //        obfStrings.put("className", "jt");
        //        /* net/minecraft/src/ItemInWorldManager */
        //        obfStrings.put("javaClassName", "jt");
        //        /* removeBlock */
        //        obfStrings.put("targetMethodName", "d");
        //        /* theWorld */
        //        obfStrings.put("worldFieldName", "a");
        //        /* thisPlayerMP */
        //        obfStrings.put("entityPlayerFieldName", "b");
        //        /* net/minecraft/src/World */
        //        obfStrings.put("worldJavaClassName", "abr");
        //        /* net/minecraft/src/World.getBlockMetadata() */
        //        obfStrings.put("getBlockMetadataMethodName", "h");
        //        /* net/minecraft/src/Block */
        //        obfStrings.put("blockJavaClassName", "aqs");
        //        /* net/minecraft/src/Block.blocksList[] */
        //        obfStrings.put("blocksListFieldName", "s");
        //        /* net/minecraft/src/EntityPlayer */
        //        obfStrings.put("entityPlayerJavaClassName", "ua");
        //        /* net/minecraft/src/EntityPlayerMP */
        //        obfStrings.put("entityPlayerMPJavaClassName", "js");
        
        /* 1.5.1/1.5.2 mappings */
        //        /* net.minecraft.src.ItemInWorldManager */
        //        obfStrings.put("className", "jd");
        //        /* net/minecraft/src/ItemInWorldManager */
        //        obfStrings.put("javaClassName", "jd");
        //        /* removeBlock */
        //        obfStrings.put("targetMethodName", "d");
        //        /* theWorld */
        //        obfStrings.put("worldFieldName", "a");
        //        /* thisPlayerMP */
        //        obfStrings.put("entityPlayerFieldName", "b");
        //        /* net/minecraft/src/World */
        //        obfStrings.put("worldJavaClassName", "aab");
        //        /* net/minecraft/src/World.getBlockMetadata() */
        //        obfStrings.put("getBlockMetadataMethodName", "h");
        //        /* net/minecraft/src/Block */
        //        obfStrings.put("blockJavaClassName", "apa");
        //        /* net/minecraft/src/Block.blocksList[] */
        //        obfStrings.put("blocksListFieldName", "r");
        //        /* net/minecraft/src/EntityPlayer */
        //        obfStrings.put("entityPlayerJavaClassName", "sq");
        //        /* net/minecraft/src/EntityPlayerMP */
        //        obfStrings.put("entityPlayerMPJavaClassName", "jc");
        
        /* 1.5.0 mappings */
        //        /* net.minecraft.src.ItemInWorldManager */
        //        obfStrings.put("className", "jd");
        //        /* net/minecraft/src/ItemInWorldManager */
        //        obfStrings.put("javaClassName", "jd");
        //        /* removeBlock */
        //        obfStrings.put("targetMethodName", "d");
        //        /* theWorld */
        //        obfStrings.put("worldFieldName", "a");
        //        /* thisPlayerMP */
        //        obfStrings.put("entityPlayerFieldName", "b");
        //        /* net/minecraft/src/World */
        //        obfStrings.put("worldJavaClassName", "zv");
        //        /* net/minecraft/src/World.getBlockMetadata() */
        //        obfStrings.put("getBlockMetadataMethodName", "h");
        //        /* net/minecraft/src/Block */
        //        obfStrings.put("blockJavaClassName", "aou");
        //        /* net/minecraft/src/Block.blocksList[] */
        //        obfStrings.put("blocksListFieldName", "r");
        //        /* net/minecraft/src/EntityPlayer */
        //        obfStrings.put("entityPlayerJavaClassName", "sk");
        //        /* net/minecraft/src/EntityPlayerMP */
        //        obfStrings.put("entityPlayerMPJavaClassName", "jc");
        
        /* 1.4.6/1.4.7 mappings */
        //        /* net.minecraft.src.ItemInWorldManager */
        //        obfStrings.put("className", "ir");
        //        /* net/minecraft/src/ItemInWorldManager */
        //        obfStrings.put("javaClassName", "ir");
        //        /* removeBlock */
        //        obfStrings.put("targetMethodName", "d");
        //        /* theWorld */
        //        obfStrings.put("worldFieldName", "a");
        //        /* thisPlayerMP */
        //        obfStrings.put("entityPlayerFieldName", "b");
        //        /* net/minecraft/src/World */
        //        obfStrings.put("worldJavaClassName", "yc");
        //        /* net/minecraft/src/World.getBlockMetadata() */
        //        obfStrings.put("getBlockMetadataMethodName", "h");
        //        /* net/minecraft/src/Block */
        //        obfStrings.put("blockJavaClassName", "amq");
        //        /* net/minecraft/src/Block.blocksList[] */
        //        obfStrings.put("blocksListFieldName", "p");
        //        /* net/minecraft/src/EntityPlayer */
        //        obfStrings.put("entityPlayerJavaClassName", "qx");
        //        /* net/minecraft/src/EntityPlayerMP */
        //        obfStrings.put("entityPlayerMPJavaClassName", "iq");
        
        /* 1.4.4 / 1.4.5 mappings */
        // /* net.minecraft.src.ItemInWorldManager */
        // obfStrings.put("className", "ir");
        // /* net/minecraft/src/ItemInWorldManager */
        // obfStrings.put("javaClassName", "ir");
        // /* removeBlock */
        // obfStrings.put("targetMethodName", "d");
        // /* theWorld */
        // obfStrings.put("worldFieldName", "a");
        // /* thisPlayerMP */
        // obfStrings.put("entityPlayerFieldName", "b");
        // /* net/minecraft/src/World */
        // obfStrings.put("worldJavaClassName", "xv");
        // /* net/minecraft/src/World.getBlockMetadata() */
        // obfStrings.put("getBlockMetadataMethodName", "h");
        // /* net/minecraft/src/Block */
        // obfStrings.put("blockJavaClassName", "amj");
        // /* net/minecraft/src/Block.blocksList[] */
        // obfStrings.put("blocksListFieldName", "p");
        // /* net/minecraft/src/EntityPlayer */
        // obfStrings.put("entityPlayerJavaClassName", "qx");
        // /* net/minecraft/src/EntityPlayerMP */
        // obfStrings.put("entityPlayerMPJavaClassName", "iq");
        
        /* 1.4.2 mappings */
        /* net.minecraft.src.ItemInWorldManager */
        // obfStrings.put("className", "ii");
        // /* net/minecraft/src/ItemInWorldManager */
        // obfStrings.put("javaClassName", "ii");
        // /* removeBlock */
        // obfStrings.put("targetMethodName", "d");
        // /* theWorld */
        // obfStrings.put("worldFieldName", "a");
        // /* thisPlayerMP */
        // obfStrings.put("entityPlayerFieldName", "b");
        // /* net/minecraft/src/World */
        // obfStrings.put("worldJavaClassName", "xe");
        // /* net/minecraft/src/World.getBlockMetadata() */
        // obfStrings.put("getBlockMetadataMethodName", "g");
        // /* net/minecraft/src/Block */
        // obfStrings.put("blockJavaClassName", "alf");
        // /* net/minecraft/src/Block.blocksList[] */
        // obfStrings.put("blocksListFieldName", "p");
        // /* net/minecraft/src/EntityPlayer */
        // obfStrings.put("entityPlayerJavaClassName", "qg");
        // /* net/minecraft/src/EntityPlayerMP */
        // obfStrings.put("entityPlayerMPJavaClassName", "ih");
        
        /* 1.3.2 mappings */
        /* net.minecraft.src.ItemInWorldManager */
        // obfStrings.put("className", "gv");
        // /* net/minecraft/src/ItemInWorldManager */
        // obfStrings.put("javaClassName", "gv");
        // /* removeBlock */
        // obfStrings.put("targetMethodName", "d");
        // /* theWorld */
        // obfStrings.put("worldFieldName", "a");
        // /* thisPlayerMP */
        // obfStrings.put("entityPlayerFieldName", "b");
        // /* net/minecraft/src/World */
        // obfStrings.put("worldJavaClassName", "up");
        // /* net/minecraft/src/World.getBlockMetadata() */
        // obfStrings.put("getBlockMetadataMethodName", "g");
        // /* net/minecraft/src/Block */
        // obfStrings.put("blockJavaClassName", "aig");
        // /* net/minecraft/src/Block.blocksList[] */
        // obfStrings.put("blocksListFieldName", "m");
        // /* net/minecraft/src/EntityPlayer */
        // obfStrings.put("entityPlayerJavaClassName", "og");
        // /* net/minecraft/src/EntityPlayerMP */
        // obfStrings.put("entityPlayerMPJavaClassName", "gu");
        
        /*
         * create a HashMap to store the MCP names of classes, methods, and fields used in the transformation
         */
        mcpStrings = new HashMap<String, String>();
        // Forge 7.7.0.582 1.5.0 mappings
        // Forge 6.6.0.497 1.4.5/1.4.6/1.4.7 mappings
        mcpStrings.put("className", "net.minecraft.item.ItemInWorldManager");
        mcpStrings.put("javaClassName", "net/minecraft/item/ItemInWorldManager");
        mcpStrings.put("targetMethodName", "removeBlock");
        mcpStrings.put("worldFieldName", "theWorld");
        mcpStrings.put("entityPlayerFieldName", "thisPlayerMP");
        mcpStrings.put("worldJavaClassName", "net/minecraft/world/World");
        mcpStrings.put("getBlockMetadataMethodName", "getBlockMetadata");
        mcpStrings.put("blockJavaClassName", "net/minecraft/block/Block");
        mcpStrings.put("blocksListFieldName", "blocksList");
        mcpStrings.put("entityPlayerJavaClassName", "net/minecraft/entity/player/EntityPlayer");
        mcpStrings.put("entityPlayerMPJavaClassName", "net/minecraft/entity/player/EntityPlayerMP");
        
        // Forge 6.4.2.408 mappings1.4.5
        // mcpStrings.put("className", "net.minecraft.src.ItemInWorldManager");
        // mcpStrings.put("javaClassName", "net/minecraft/src/ItemInWorldManager");
        // mcpStrings.put("targetMethodName", "removeBlock");
        // mcpStrings.put("worldFieldName", "theWorld");
        // mcpStrings.put("entityPlayerFieldName", "thisPlayerMP");
        // mcpStrings.put("worldJavaClassName", "net/minecraft/src/World");
        // mcpStrings.put("getBlockMetadataMethodName", "getBlockMetadata");
        // mcpStrings.put("blockJavaClassName", "net/minecraft/src/Block");
        // mcpStrings.put("blocksListFieldName", "blocksList");
        // mcpStrings.put("entityPlayerJavaClassName", "net/minecraft/src/EntityPlayer");
        // mcpStrings.put("entityPlayerMPJavaClassName", "net/minecraft/src/EntityPlayerMP");
    }
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        // TCLog.info("transforming: "+name);
        if (name.equals(obfStrings.get("className")))
        {
            return transformItemInWorldManager(bytes, obfStrings);
        }
        else if (name.equals(mcpStrings.get("className")))
        {
            return transformItemInWorldManager(bytes, mcpStrings);
        }
        
        return bytes;
    }
    
    private byte[] transformItemInWorldManager(byte[] bytes, HashMap<String, String> hm)
    {
        //        BBLog.info("BlockBreaker ASM Magic Time!");
        //        BBLog.info("Class Transformation running on " + hm.get("javaClassName") + "...");
        
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        
        // find method to inject into
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext())
        {
            MethodNode m = methods.next();
            if (m.name.equals(hm.get("targetMethodName")) && m.desc.equals(targetMethodDesc))
            {
                // BBLog.info("Found target method " + m.name + m.desc + "! Searching for landmarks...");
                int blockIndex = 4;
                int mdIndex = 5;
                
                // find injection point in method (use IFNULL inst)
                for (int index = 0; index < m.instructions.size(); index++)
                {
                    // BBLog.info("Processing INSN at " + index +
                    //                     " of type " + m.instructions.get(index).getType() +
                    //                     ", OpCode " + m.instructions.get(index).getOpcode());
                    // find local Block object node and from that, local object index
                    if (m.instructions.get(index).getType() == AbstractInsnNode.FIELD_INSN)
                    {
                        FieldInsnNode blocksListNode = (FieldInsnNode) m.instructions.get(index);
                        //                        BBLog.info("Owner: %s    Name: %s", blocksListNode.owner, blocksListNode.name);
                        if (blocksListNode.owner.equals(hm.get("blockJavaClassName")) && blocksListNode.name.equals(hm.get("blocksListFieldName")))
                        {
                            int offset = 1;
                            while (m.instructions.get(index + offset).getOpcode() != ASTORE)
                                offset++;
                            //                             BBLog.info("Found Block object ASTORE Node at " + (index + offset));
                            VarInsnNode blockNode = (VarInsnNode) m.instructions.get(index + offset);
                            blockIndex = blockNode.var;
                            //                             BBLog.info("Block object is in local object " + blockIndex);
                        }
                    }
                    
                    // find local metadata variable node and from that, local
                    // variable index
                    if (m.instructions.get(index).getType() == AbstractInsnNode.METHOD_INSN)
                    {
                        MethodInsnNode mdNode = (MethodInsnNode) m.instructions.get(index);
                        //                        BBLog.info("Owner: %s    Name: %s", mdNode.owner, mdNode.name);
                        if (mdNode.owner.equals(hm.get("worldJavaClassName")) && mdNode.name.equals(hm.get("getBlockMetadataMethodName")))
                        {
                            int offset = 1;
                            while (m.instructions.get(index + offset).getOpcode() != ISTORE)
                                offset++;
                            //                             BBLog.info("Found metadata local variable ISTORE Node at " + (index + offset));
                            VarInsnNode mdFieldNode = (VarInsnNode) m.instructions.get(index + offset);
                            mdIndex = mdFieldNode.var;
                            //                             BBLog.info("Metadata is in local variable " + mdIndex);
                        }
                    }
                    
                    if (m.instructions.get(index).getOpcode() == IFNULL)
                    {
                        // BBLog.info("Found IFNULL Node at " + index);
                        
                        int offset = 1;
                        while (m.instructions.get(index + offset).getOpcode() != ALOAD)
                            offset++;
                        
                        //                        BBLog.info("Found ALOAD Node at offset " + offset + " from IFNULL Node");
                        //                        BBLog.info("Patching method " + (String) hm.get("javaClassName") + "/" + m.name + m.desc + "...");
                        
                        // make a new label node for the end of our code
                        LabelNode lmm1Node = new LabelNode(new Label());
                        
                        // make new instruction list
                        InsnList toInject = new InsnList();
                        
                        // construct instruction nodes for list
                        toInject.add(new FieldInsnNode(GETSTATIC, "bspkrs/blockbreaker/BlockBreakerMod", "instance", "Lbspkrs/blockbreaker/BlockBreakerMod;"));
                        toInject.add(new VarInsnNode(ALOAD, 0));
                        toInject.add(new FieldInsnNode(GETFIELD, hm.get("javaClassName"), hm.get("worldFieldName"), "L" + hm.get("worldJavaClassName") + ";"));
                        toInject.add(new VarInsnNode(ILOAD, 1));
                        toInject.add(new VarInsnNode(ILOAD, 2));
                        toInject.add(new VarInsnNode(ILOAD, 3));
                        toInject.add(new VarInsnNode(ALOAD, blockIndex));
                        toInject.add(new VarInsnNode(ILOAD, mdIndex));
                        toInject.add(new VarInsnNode(ALOAD, 0));
                        toInject.add(new FieldInsnNode(GETFIELD, hm.get("javaClassName"), hm.get("entityPlayerFieldName"), "L" + hm.get("entityPlayerMPJavaClassName") + ";"));
                        toInject.add(new MethodInsnNode(INVOKEVIRTUAL, "bspkrs/blockbreaker/BlockBreakerMod", "onBlockHarvested",
                                "(L" + hm.get("worldJavaClassName") + ";IIIL" + hm.get("blockJavaClassName") + ";IL"
                                        + hm.get("entityPlayerJavaClassName") + ";)V"));
                        toInject.add(lmm1Node);
                        
                        m.instructions.insertBefore(m.instructions.get(index + offset), toInject);
                        
                        //  BBLog.info("Method " + (String) hm.get("javaClassName") + "/" + m.name + m.desc + " patched at index " + (index + offset - 1));
                        BBLog.info("BlockBreaker ASM Patching Complete!");
                        BlockBreakerMod.isCoreModLoaded = true;
                        break;
                    }
                }
            }
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
