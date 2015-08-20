package shukaro.questlog.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;
import shukaro.questlog.Questlog;

import java.io.File;
import java.io.IOException;

public class QuestData
{
    private JsonArray data;
    private File dataFile;

    public QuestData(File dataFile)
    {
        this.dataFile = dataFile;
        try
        {
            this.load();
            this.save();
        }
        catch (IOException e)
        {
            Questlog.logger.warn("Problem accessing quest data file");
            e.printStackTrace();
        }
    }

    public boolean load() throws IOException
    {
        if (!this.dataFile.exists() && this.dataFile.createNewFile())
            return true;
        else if (this.dataFile.exists())
        {
            return true;
        }
        return false;
    }

    public boolean save() throws IOException
    {
        return false;
    }

}
