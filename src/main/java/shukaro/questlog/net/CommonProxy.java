package shukaro.questlog.net;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import shukaro.questlog.QuestConfig;
import shukaro.questlog.data.BookData;
import shukaro.questlog.data.QuestData;
import shukaro.questlog.Questlog;

import java.io.File;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent evt)
    {
        Questlog.logger = evt.getModLog();
        QuestConfig.initCommon(evt);
        Questlog.questData = new QuestData(new File(evt.getModConfigurationDirectory(), Questlog.modID + File.separator + "questData.json"));
        Questlog.bookData = new BookData(new File(evt.getModConfigurationDirectory(), Questlog.modID + File.separator + "bookData.json"));
    }

    public void init(FMLInitializationEvent evt)
    {

    }

    public void postInit(FMLPostInitializationEvent evt)
    {

    }
}
