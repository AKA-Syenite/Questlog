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
import java.util.Locale;

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

    protected JsonArray getPageNodes(JsonObject page)
    {
        return page.getAsJsonArray("nodes");
    }

    public ArrayList<String> getPageNodeIDs(String uid)
    {
        JsonObject node = getPage(uid);
        ArrayList<String> out = new ArrayList<String>();
        for (JsonElement e : getPageNodes(node))
            out.add(e.getAsString());
        return out;
    }

    protected JsonObject getPageNode(JsonObject page, String uid)
    {
        for (JsonElement node : getPageNodes(page))
        {
            if (node.getAsJsonObject().get("uid").getAsString().equals(uid))
                return node.getAsJsonObject();
        }
        return null;
    }

    public NodeTypes getTypeForNode(String pageUID, String nodeUID)
    {
        JsonObject node = getPageNode(getPage(pageUID), nodeUID);
        return toNodeType(node.get("type").getAsString());
    }

    public ArrayList<String> getNodeParents(String pageUID, String nodeUID)
    {
        JsonObject node = getPageNode(getPage(pageUID), nodeUID);
        ArrayList<String> out = new ArrayList<String>();
        JsonArray parents = node.get("parents").getAsJsonArray();
        for (JsonElement e : parents)
            out.add(e.getAsString());
        return out;
    }

    public ArrayList<String> getNodeTags(String pageUID, String nodeUID)
    {
        JsonObject node = getPageNode(getPage(pageUID), nodeUID);
        ArrayList<String> out = new ArrayList<String>();
        JsonArray tags = node.get("tags").getAsJsonArray();
        for (JsonElement e : tags)
            out.add(e.getAsString());
        return out;
    }

    public int[] getNodePos(String pageUID, String nodeUID)
    {
        JsonObject node = getPageNode(getPage(pageUID), nodeUID);
        int[] pos = new int[2];
        pos[0] = node.get("x").getAsInt();
        pos[1] = node.get("y").getAsInt();
        return pos;
    }

    public int[] getSecondaryPosForLineNode(String pageUID, String nodeUID)
    {
        JsonObject node = getPageNode(getPage(pageUID), nodeUID);
        int[] pos = new int[2];
        if (toNodeType(node.get("type").getAsString()) == NodeTypes.LINE)
        {
            pos[0] = node.get("x2").getAsInt();
            pos[1] = node.get("y2").getAsInt();
        }
        return pos;
    }

    public String getTargetForPageNode(String pageUID, String nodeUID)
    {
        JsonObject node = getPageNode(getPage(pageUID), nodeUID);
        if (toNodeType(node.get("type").getAsString()) == NodeTypes.PAGE)
            return node.get("target").getAsString();
        return "";
    }

    public NodeTypes toNodeType(String type)
    {
        if (type.toLowerCase(Locale.ENGLISH).equals("quest"))
            return NodeTypes.QUEST;
        else if (type.toLowerCase(Locale.ENGLISH).equals("line"))
            return NodeTypes.LINE;
        else if (type.toLowerCase(Locale.ENGLISH).equals("page"))
            return NodeTypes.PAGE;
        else
            return NodeTypes.UNKNOWN;
    }

    public enum NodeTypes
    {
        QUEST, LINE, PAGE, UNKNOWN;
    }
}