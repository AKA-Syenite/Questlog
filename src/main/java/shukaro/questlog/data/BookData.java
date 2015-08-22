package shukaro.questlog.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import shukaro.questlog.Questlog;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class BookData
{
    private JsonArray data;
    private File dataFile;

    private static ResourceLocation templateFile = new ResourceLocation("questlog:templates/bookData.json");

    public BookData(File dataFile)
    {
        this.dataFile = dataFile;
        try
        {
            this.load();
        }
        catch (IOException e)
        {
            Questlog.logger.warn("Problem accessing book data file");
            e.printStackTrace();
        }
    }

    public void load() throws IOException
    {
        if (!this.dataFile.exists())
        {
            Files.copy(Minecraft.getMinecraft().getResourceManager().getResource(templateFile).getInputStream(), this.dataFile.toPath());
            data = new JsonArray();
        }
        else
            data = Questlog.gson.fromJson(new BufferedReader(new FileReader(this.dataFile)), JsonObject.class).getAsJsonArray("pages");
    }

    public void save() throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(this.dataFile));
        out.write(data.toString());
        out.close();
    }

    public JsonObject getPage(String uid)
    {
        for (int i=0; i<data.size(); i++)
        {
            if (data.get(i).getAsJsonObject().get("uid").toString().equals("uid"))
                return data.get(i).getAsJsonObject();
        }
        return null;
    }

    public ArrayList<String> getPages()
    {
        ArrayList<String> out = new ArrayList<String>();
        for (int i=0; i<data.size(); i++)
            out.add(data.get(i).getAsJsonObject().get("uid").getAsString());
        return out;
    }

    public boolean removePage(String uid)
    {
        JsonArray newArray = new JsonArray();
        for (int i=0; i<data.size(); i++)
        {
            if (!data.get(i).getAsJsonObject().get("uid").toString().equals("uid"))
                newArray.add(data.get(i));
        }
        if (data.size() == newArray.size())
            return false;
        this.data = newArray;
        return true;
    }

    public ArrayList<String> getPageNodes(String uid)
    {
        JsonObject node = getPage(uid);
        ArrayList<String> out = new ArrayList<String>();
        for (JsonElement e : getPageNodes(node))
            out.add(e.getAsString());
        return out;
    }

    public JsonArray getPageNodes(JsonObject page)
    {
        return page.getAsJsonArray("nodes");
    }

    public JsonObject getPageNode(JsonObject page, String uid)
    {
        for (JsonElement node : getPageNodes(page))
        {
            if (node.getAsJsonObject().get("uid").getAsString().equals(uid))
                return node.getAsJsonObject();
        }
        return null;
    }

}