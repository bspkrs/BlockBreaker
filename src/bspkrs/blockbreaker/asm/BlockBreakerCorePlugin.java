package bspkrs.blockbreaker.asm;

import java.util.Map;

import bspkrs.util.Const;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

/*
 * Don't let any access transformer stuff accidentally modify our classes. A list of package prefixes for FML to ignore
 */
@TransformerExclusions({ "bspkrs.blockbreaker.asm" })
@MCVersion(value = Const.MCVERSION)
public class BlockBreakerCorePlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getLibraryRequestClass()
    {
        return null;
    }
    
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] { "bspkrs.blockbreaker.asm.ItemInWorldManagerTransformer" };
    }
    
    @Override
    public String getModContainerClass()
    {
        return null;
    }
    
    @Override
    public String getSetupClass()
    {
        return null;
    }
    
    @Override
    public void injectData(Map<String, Object> data)
    {   
        
    }
    
}
