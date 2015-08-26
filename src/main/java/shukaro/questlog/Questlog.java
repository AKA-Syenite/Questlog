package shukaro.questlog;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import org.apache.logging.log4j.Logger;
import shukaro.questlog.net.CommonProxy;

@Mod(modid = Questlog.modID, useMetadata = true, dependencies = "required-after:CoFHCore")
public class Questlog
{
    @SidedProxy(clientSide = "shukaro.questlog.net.ClientProxy", serverSide = "shukaro.questlog.net.CommonProxy")
    public static CommonProxy proxy;

    public static final String modID = "Questlog";
    public static String version = "";

    public static Logger logger;

    public static Gson gson = new Gson();
    public static JsonParser parser = new JsonParser();

    @Mod.Instance(modID)
    public static Questlog instance;

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent evt)
    {
        proxy.serverStarting(evt);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent evt)
    {
        proxy.serverStarted(evt);
    }

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
