package shukaro.questlog.net;

import cpw.mods.fml.common.event.*;
import shukaro.questlog.QuestConfig;

public class ClientProxy extends CommonProxy
{
    @Override
    public void serverStarting(FMLServerStartingEvent evt)
    {
        super.serverStarting(evt);
    }

    @Override
    public void serverStarted(FMLServerStartedEvent evt)
    {
        super.serverStarted(evt);
    }

    @Override
    public void preInit(FMLPreInitializationEvent evt)
    {
        super.preInit(evt);
        QuestConfig.initClient(evt);
    }

    @Override
    public void init(FMLInitializationEvent evt)
    {
        super.init(evt);
    }

    @Override
    public void postInit(FMLPostInitializationEvent evt)
    {
        super.postInit(evt);
    }
}
