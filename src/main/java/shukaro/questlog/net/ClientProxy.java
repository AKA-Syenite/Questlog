package shukaro.questlog.net;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import shukaro.questlog.QuestConfig;

public class ClientProxy extends CommonProxy
{
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
