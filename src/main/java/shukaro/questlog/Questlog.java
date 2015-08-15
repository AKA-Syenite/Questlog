package shukaro.questlog;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import shukaro.questlog.net.CommonProxy;

@Mod(modid = Questlog.modID, name = Questlog.modName, version = Questlog.modVersion,
        dependencies = "")
public class Questlog
{
    @SidedProxy(clientSide = "shukaro.questlog.net.ClientProxy", serverSide = "shukaro.questlog.net.CommonProxy")
    public static CommonProxy proxy;

    public static final String modID = "Questlog";
    public static final String modName = "Questlog";
    public static final String modChannel = "Questlog";
    public static final String modVersion = "0.1";

    public static Logger logger;

    @Mod.Instance(modID)
    public static Questlog instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt)
    {
        proxy.preInit(evt);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt)
    {
        proxy.init(evt);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt)
    {
        proxy.postInit(evt);
    }
}
