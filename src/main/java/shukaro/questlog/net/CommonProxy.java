package shukaro.questlog.net;

import cpw.mods.fml.common.event.*;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import shukaro.questlog.QuestConfig;
import shukaro.questlog.data.BookData;
import shukaro.questlog.data.PlayerData;
import shukaro.questlog.data.QuestData;
import shukaro.questlog.Questlog;
import shukaro.questlog.event.QuestEventHandler;

import java.io.File;

public class CommonProxy
{
    public void serverStarted(FMLServerStartedEvent evt)
    {
        Questlog.playerData = new PlayerData(new File(MinecraftServer.getServer().worldServers[0].getSaveHandler().getWorldDirectory(), "questlogPlayerData.json"));
    }

    public void preInit(FMLPreInitializationEvent evt)
    {
        Questlog.logger = evt.getModLog();
        QuestConfig.initCommon(evt);
        Questlog.questData = new QuestData(new File(evt.getModConfigurationDirectory(), Questlog.modID + File.separator + "questData.json"));
        Questlog.bookData = new BookData(new File(evt.getModConfigurationDirectory(), Questlog.modID + File.separator + "bookData.json"));
        MinecraftForge.EVENT_BUS.register(new QuestEventHandler());
    }

    public void init(FMLInitializationEvent evt)
    {

    }

    public void postInit(FMLPostInitializationEvent evt)
    {

    }
}
