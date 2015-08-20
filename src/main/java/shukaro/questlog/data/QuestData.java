package shukaro.questlog.data;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import shukaro.questlog.Questlog;

import java.io.*;
import java.nio.file.Files;

public class QuestData
{
    private JsonObject data;
    private File dataFile;

    private static ResourceLocation templateFile = new ResourceLocation("questlog:templates/questData.json");

    public QuestData(File dataFile)
    {
        this.dataFile = dataFile;
        try
        {
            this.load();
        }
        catch (IOException e)
        {
            Questlog.logger.warn("Problem accessing quest data file");
            e.printStackTrace();
        }
    }

    public void load() throws IOException
    {
        if (!this.dataFile.exists())
        {
            Files.copy(Minecraft.getMinecraft().getResourceManager().getResource(templateFile).getInputStream(), this.dataFile.toPath());
            data = new JsonObject();
        }
        else
            data = Questlog.gson.fromJson(new BufferedReader(new FileReader(this.dataFile)), JsonObject.class);
    }

    public void save() throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(this.dataFile));
        out.write(data.toString());
        out.close();
    }

}
