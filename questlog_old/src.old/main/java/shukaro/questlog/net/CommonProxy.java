package shukaro.questlog.net;

import cpw.mods.fml.common.event.*;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import shukaro.questlog.QuestConfig;
import shukaro.questlog.command.CommandHandler;
import shukaro.questlog.data.BookData;
import shukaro.questlog.data.PlayerData;
import shukaro.questlog.data.QuestData;
import shukaro.questlog.Questlog;
import shukaro.questlog.data.questing.QuestManager;
import shukaro.questlog.data.questing.objectives.ObjectiveHaveItem;
import shukaro.questlog.data.questing.objectives.ObjectiveKillEntity;
import shukaro.questlog.event.QuestEventHandler;

import java.io.File;

public class CommonProxy
{
    public void serverStarting(FMLServerStartingEvent evt)
    {
        evt.registerServerCommand(new CommandHandler());
    }

    public void serverStarted(FMLServerStartedEvent evt)
    {
        PlayerData.init(new File(MinecraftServer.getServer().worldServers[0].getSaveHandler().getWorldDirectory(), "questlogPlayerData.json"));
    }

    public void preInit(FMLPreInitializationEvent evt)
    {
        Questlog.logger = evt.getModLog();
        Questlog.version = evt.getModMetadata().version;
        QuestConfig.initCommon(evt);
        QuestData.init(new File(evt.getModConfigurationDirectory(), Questlog.modID + File.separator + "questData.json"));
        BookData.init(new File(evt.getModConfigurationDirectory(), Questlog.modID + File.separator + "bookData.json"));
        MinecraftForge.EVENT_BUS.register(new QuestEventHandler());
        registerObjectives();
        registerRewards();
    }

    public void init(FMLInitializationEvent evt)
    {

    }

    public void postInit(FMLPostInitializationEvent evt)
    {

    }

    private void registerObjectives()
    {
        QuestManager.registerObjective(ObjectiveKillEntity.class, "killEntity", 3);
        QuestManager.registerObjective(ObjectiveHaveItem.class, "haveItem", 2);
    }

    private void registerRewards()
    {

    }
}
