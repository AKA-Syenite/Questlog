package shukaro.questlog.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonNodeReader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import shukaro.questlog.Questlog;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

public class QuestData
{
    private static JsonArray data;
    private static File dataFile;

    private static ResourceLocation schemaFile = new ResourceLocation("questlog:schema/questData.json");

    public static void init(File file)
    {
        dataFile = file;
        try
        {
            load();
            validate();
        }
        catch (IOException e)
        {
            Questlog.logger.warn("Problem accessing quest data file");
            e.printStackTrace();
        }
    }

    public static void validate() throws IOException
    {
        Questlog.logger.info("Validating quest data");
        JsonNode schema = new JsonNodeReader().fromInputStream(Minecraft.getMinecraft().getResourceManager().getResource(schemaFile).getInputStream());
        JsonNode json = new JsonNodeReader().fromReader(new BufferedReader(new FileReader(dataFile)));
        ProcessingReport report = JsonSchemaFactory.byDefault().getValidator().validateUnchecked(schema, json);
        if (!report.isSuccess())
        {
            Iterator<ProcessingMessage> pit = report.iterator();
            while (pit.hasNext())
                Questlog.logger.warn(pit.next().toString());
            Questlog.logger.warn("Quest data json was invalid, aborting");
            data = new JsonArray();
        }
        else
        {
            Questlog.logger.info("Sucessfully validated quest data");
            save();
        }
    }

    public static void load() throws IOException
    {
        if (!dataFile.exists())
        {
            dataFile.createNewFile();
            data = new JsonArray();
        }
        else
            data = Questlog.gson.fromJson(new BufferedReader(new FileReader(dataFile)), JsonObject.class).getAsJsonArray("quests");
    }

    public static void save() throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(dataFile));
        JsonObject temp = new JsonObject();
        temp.add("quests", data);
        out.write(temp.toString());
        out.close();
    }

    public static JsonObject getQuest(String uid)
    {
        for (int i=0; i<data.size(); i++)
        {
            if (data.get(i).getAsJsonObject().get("uid").getAsString().equals(uid))
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
            if (!data.get(i).getAsJsonObject().get("uid").getAsString().equals(uid))
                newArray.add(data.get(i));
        }
        if (data.size() == newArray.size())
            return false;
        data = newArray;
        try
        {
            save();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public static void createQuest(String questUID, String[] objectives, String[] rewards, String[] tags)
    {
        JsonObject quest = new JsonObject();
        quest.add("uid", new JsonPrimitive(questUID));
        quest.add("objectives", Questlog.parser.parse(Questlog.gson.toJson(objectives)).getAsJsonArray());
        quest.add("rewards", Questlog.parser.parse(Questlog.gson.toJson(rewards)).getAsJsonArray());
        quest.add("tags", Questlog.parser.parse(Questlog.gson.toJson(tags)).getAsJsonArray());
        data.add(quest);
        try
        {
            save();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

    public static void setQuestUID(String oldUID, String newUID)
    {
        JsonObject quest = getQuest(oldUID);
        quest.remove("uid");
        quest.add("uid", new JsonPrimitive(newUID));
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

    public static boolean hasTag(String questUID, String tag)
    {
        return getQuestTags(questUID).contains(tag);
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
