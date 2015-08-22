package shukaro.questlog.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import shukaro.questlog.Questlog;

import java.io.*;
import java.nio.file.Files;

public class QuestData
{
    private JsonArray data;
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
            data = new JsonArray();
        }
        else
            data = Questlog.gson.fromJson(new BufferedReader(new FileReader(this.dataFile)), JsonObject.class).getAsJsonArray("quests");
    }

    public void save() throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(this.dataFile));
        out.write(data.toString());
        out.close();
    }

    public JsonObject getQuest(String uid)
    {
        for (int i=0; i<data.size(); i++)
        {
            if (data.get(i).getAsJsonObject().get("uid").toString().equals("uid"))
                return data.get(i).getAsJsonObject();
        }
        return null;
    }

    public boolean removeQuest(String uid)
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

    protected JsonArray getQuestObjectives(JsonObject quest)
    {
        return quest.getAsJsonArray("objectives");
    }

    public JsonArray getQuestObjectives(String uid)
    {
        return getQuestObjectives(getQuest(uid));
    }

    protected void setQuestObjectives(JsonObject quest, JsonArray objectives)
    {
        quest.remove("objectives");
        quest.add("objectives", objectives);
    }

    public void setQuestObjectives(String uid, String[] objectives)
    {
        setQuestObjectives(getQuest(uid), Questlog.parser.parse(Questlog.gson.toJson(objectives)).getAsJsonArray());
    }

    protected JsonArray getQuestRewards(JsonObject quest)
    {
        return quest.getAsJsonArray("rewards");
    }

    public JsonArray getQuestRewards(String uid)
    {
        return getQuestRewards(getQuest(uid));
    }

    protected void setQuestRewards(JsonObject quest, JsonArray rewards)
    {
        quest.remove("rewards");
        quest.add("rewards", rewards);
    }

    public void setQuestRewards(String uid, String[] rewards)
    {
        setQuestRewards(getQuest(uid), Questlog.parser.parse(Questlog.gson.toJson(rewards)).getAsJsonArray());
    }

    protected JsonArray getQuestTags(JsonObject quest)
    {
        return quest.getAsJsonArray("tags");
    }

    public JsonArray getQuestTags(String uid)
    {
        return getQuestTags(getQuest(uid));
    }

    protected void setQuestTags(JsonObject quest, JsonArray tags)
    {
        quest.remove("tags");
        quest.add("tags", tags);
    }

    public void setQuestTags(String uid, String[] tags)
    {
        setQuestTags(getQuest(uid), Questlog.parser.parse(Questlog.gson.toJson(tags)).getAsJsonArray());
    }

}
