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

public class QuestData
{
    private static JsonArray data;
    private static File dataFile;

    private static ResourceLocation templateFile = new ResourceLocation("questlog:templates/questData.json");

    public QuestData(File file)
    {
        dataFile = file;
        try
        {
            load();
        }
        catch (IOException e)
        {
            Questlog.logger.warn("Problem accessing quest data file");
            e.printStackTrace();
        }
    }

    public static void load() throws IOException
    {
        if (!dataFile.exists())
        {
            Files.copy(Minecraft.getMinecraft().getResourceManager().getResource(templateFile).getInputStream(), dataFile.toPath());
            data = new JsonArray();
        }
        else
            data = Questlog.gson.fromJson(new BufferedReader(new FileReader(dataFile)), JsonObject.class).getAsJsonArray("quests");
    }

    public static void save() throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(dataFile));
        out.write(data.toString());
        out.close();
    }

    public static JsonObject getQuest(String uid)
    {
        for (int i=0; i<data.size(); i++)
        {
            if (data.get(i).getAsJsonObject().get("uid").getAsString().equals("uid"))
                return data.get(i).getAsJsonObject();
        }
        return null;
    }

    public static ArrayList<String> getQuestIDs()
    {
        ArrayList<String> out = new ArrayList<String>();
        for (int i=0; i<data.size(); i++)
            out.add(data.get(i).getAsJsonObject().get("uid").getAsString());
        return out;
    }

    public static boolean removeQuest(String uid)
    {
        JsonArray newArray = new JsonArray();
        for (int i=0; i<data.size(); i++)
        {
            if (!data.get(i).getAsJsonObject().get("uid").toString().equals("uid"))
                newArray.add(data.get(i));
        }
        if (data.size() == newArray.size())
            return false;
        data = newArray;
        return true;
    }

    protected static JsonArray getQuestObjectives(JsonObject quest)
    {
        return quest.getAsJsonArray("objectives");
    }

    public static ArrayList<String> getQuestObjectives(String uid)
    {
        JsonArray obj = getQuestObjectives(getQuest(uid));
        ArrayList<String> out = new ArrayList<String>();
        for (JsonElement e : obj)
            out.add(e.getAsString());
        return out;
    }

    protected static void setQuestObjectives(JsonObject quest, JsonArray objectives)
    {
        quest.remove("objectives");
        quest.add("objectives", objectives);
    }

    public static void setQuestObjectives(String uid, String[] objectives)
    {
        setQuestObjectives(getQuest(uid), Questlog.parser.parse(Questlog.gson.toJson(objectives)).getAsJsonArray());
    }

    protected static JsonArray getQuestRewards(JsonObject quest)
    {
        return quest.getAsJsonArray("rewards");
    }

    public static ArrayList<String> getQuestRewards(String uid)
    {
        JsonArray obj = getQuestRewards(getQuest(uid));
        ArrayList<String> out = new ArrayList<String>();
        for (JsonElement e : obj)
            out.add(e.getAsString());
        return out;
    }

    protected static void setQuestRewards(JsonObject quest, JsonArray rewards)
    {
        quest.remove("rewards");
        quest.add("rewards", rewards);
    }

    public static void setQuestRewards(String uid, String[] rewards)
    {
        setQuestRewards(getQuest(uid), Questlog.parser.parse(Questlog.gson.toJson(rewards)).getAsJsonArray());
    }

    protected static JsonArray getQuestTags(JsonObject quest)
    {
        return quest.getAsJsonArray("tags");
    }

    public static ArrayList<String> getQuestTags(String uid)
    {
        JsonArray obj = getQuestTags(getQuest(uid));
        ArrayList<String> out = new ArrayList<String>();
        for (JsonElement e : obj)
            out.add(e.getAsString());
        return out;
    }

    protected static void setQuestTags(JsonObject quest, JsonArray tags)
    {
        quest.remove("tags");
        quest.add("tags", tags);
    }

    public static void setQuestTags(String uid, String[] tags)
    {
        setQuestTags(getQuest(uid), Questlog.parser.parse(Questlog.gson.toJson(tags)).getAsJsonArray());
    }

}
