package shukaro.questlog;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Locale;

public class QuestConfig
{
    public static int questlogYPos;
    public static int questlogXPos;

    public static void initCommon(FMLPreInitializationEvent evt)
    {
        Configuration c = new Configuration(new File(evt.getModConfigurationDirectory(), Questlog.modID + File.separator + Questlog.modID.toLowerCase(Locale.ENGLISH) + ".cfg"));
        try
        {
            c.load();
        }
        catch (Exception e)
        {
            Questlog.logger.warn("Could not load the config file");
            e.printStackTrace();
        }
        finally
        {
            c.save();
        }
    }

    public static void initClient(FMLPreInitializationEvent evt)
    {
        Configuration c = new Configuration(new File(evt.getModConfigurationDirectory(), Questlog.modID + File.separator + Questlog.modID.toLowerCase(Locale.ENGLISH) + ".cfg"));
        try
        {
            c.load();
            questlogXPos = c.getInt("Questlog X Position", "Client", 10, 0, 10000, "");
            questlogYPos = c.getInt("Questlog Y Position", "Client", 10, 0, 10000, "");
        }
        catch (Exception e)
        {
            Questlog.logger.warn("Could not load the config file");
            e.printStackTrace();
        }
        finally
        {
            c.save();
        }
    }
}
