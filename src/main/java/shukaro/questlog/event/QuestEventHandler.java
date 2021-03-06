package shukaro.questlog.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;
import shukaro.questlog.Questlog;
import shukaro.questlog.data.PlayerData;

import java.io.IOException;

public class QuestEventHandler
{
    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save e)
    {
        try
        {
            PlayerData.save();
        }
        catch (IOException ex)
        {
            Questlog.logger.warn("Problem saving player data");
            ex.printStackTrace();
        }
    }
}
