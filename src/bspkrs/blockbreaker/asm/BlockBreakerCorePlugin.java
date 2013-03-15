package bspkrs.blockbreaker.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

/*
 * Don't let any access transformer stuff accidentally modify our classes. A list of package prefixes for FML to ignore
 */
@TransformerExclusions({ "bspkrs.blockbreaker.asm" })
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
