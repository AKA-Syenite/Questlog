package shukaro.questlog;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import org.apache.logging.log4j.Logger;
import shukaro.questlog.data.BookData;
import shukaro.questlog.data.PlayerData;
import shukaro.questlog.data.QuestData;
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

    public static Gson gson = new Gson();
    public static JsonParser parser = new JsonParser();

    @Mod.Instance(modID)
    public static Questlog instance;

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent evt)
    {

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
