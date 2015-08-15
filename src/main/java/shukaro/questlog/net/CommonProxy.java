package shukaro.questlog.net;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import shukaro.questlog.QuestConfig;
import shukaro.questlog.Questlog;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent evt)
    {
        Questlog.logger = evt.getModLog();
        QuestConfig.initCommon(evt);
    }

    public void init(FMLInitializationEvent evt)
    {

    }

    public void postInit(FMLPostInitializationEvent evt)
    {

    }
}
