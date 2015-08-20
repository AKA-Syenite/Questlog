package shukaro.questlog.data;

import com.google.gson.JsonObject;
import shukaro.questlog.Questlog;

import java.io.*;

public class PlayerData
{
    private JsonObject data;
    private File dataFile;

    public PlayerData(File dataFile)
    {
        this.dataFile = dataFile;
        try
        {
            this.load();
        }
        catch (IOException e)
        {
            Questlog.logger.warn("Problem accessing player data file");
            e.printStackTrace();
        }
    }

    public void load() throws IOException
    {
        if (!this.dataFile.exists())
        {
            this.dataFile.createNewFile();
            data = new JsonObject();
        }
        else
            data = Questlog.gson.fromJson(new BufferedReader(new FileReader(this.dataFile)), JsonObject.class);
    }

    public void save() throws IOException
    {
        if (data != null)
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(this.dataFile));
            out.write(data.toString());
            out.close();
        }
    }
}
