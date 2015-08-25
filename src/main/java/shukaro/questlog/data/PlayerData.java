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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import shukaro.questlog.Questlog;
import shukaro.questlog.data.questing.AbstractObjective;
import shukaro.questlog.data.questing.QuestManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PlayerData
{
    private static JsonArray data;
    private static File dataFile;

    private static ResourceLocation schemaFile = new ResourceLocation("questlog:schema/playerData.json");

    public PlayerData(File file)
    {
        dataFile = file;
        try
        {
            load();
            validate();
        }
        catch (IOException e)
        {
            Questlog.logger.warn("Problem accessing player data file");
            e.printStackTrace();
        }
    }

    public static void validate() throws IOException
    {
        Questlog.logger.info("Validating player data");
        JsonNode schema = new JsonNodeReader().fromInputStream(Minecraft.getMinecraft().getResourceManager().getResource(schemaFile).getInputStream());
        JsonNode json = new JsonNodeReader().fromReader(new BufferedReader(new FileReader(dataFile)));
        ProcessingReport report = JsonSchemaFactory.byDefault().getValidator().validateUnchecked(schema, json);
        if (!report.isSuccess())
        {
            Iterator<ProcessingMessage> pit = report.iterator();
            while (pit.hasNext())
                Questlog.logger.warn(pit.next().toString());
            Questlog.logger.warn("Player data json was invalid, aborting");
            data = new JsonArray();
        }
        else
            Questlog.logger.info("Sucessfully validated player data");
    }

    public static void load() throws IOException
    {
        if (!dataFile.exists())
        {
            dataFile.createNewFile();
            data = new JsonArray();
            save();
        }
        else
            data = Questlog.gson.fromJson(new BufferedReader(new FileReader(dataFile)), JsonObject.class).get("players").getAsJsonArray();
    }

    public static void save() throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(dataFile));
        JsonObject jo = new JsonObject();
        for (Map.Entry<UUID, ArrayList<AbstractObjective>> entry : QuestManager.runningObjectives.entrySet())
        {
            for (AbstractObjective obj : entry.getValue())
            {
                updateQuestArgs(entry.getKey(), obj.parentQuest, obj.saveToStringArray());
            }
        }
        jo.add("players", data);
        out.write(jo.toString());
        out.close();
    }

    public static JsonObject getPlayerData(EntityPlayer player)
    {
        return getPlayerData(player.getPersistentID());
    }

    public static JsonObject getPlayerData(UUID playerUUID)
    {
        Iterator<JsonElement> playerIT = data.iterator();
        JsonObject player = null;
        while (playerIT.hasNext())
        {
            player = (JsonObject)playerIT.next();
            if (player.get("uuid").getAsString().equals(playerUUID.toString()))
                break;
        }
        if (player == null)
        {
            JsonObject newPlayer = new JsonObject();
            newPlayer.add("uuid", new JsonPrimitive(playerUUID.toString()));
            newPlayer.add("quests", new JsonArray());
            data.add(newPlayer);
            player = newPlayer;
        }
        return player;
    }

    public static void giveQuest(EntityPlayer player, String questUID)
    {
        giveQuest(player.getPersistentID(), questUID);
    }

    public static void giveQuest(UUID uuid, String questUID)
    {
        JsonObject player = getPlayerData(uuid);
        JsonObject questTemplate = QuestData.getQuest(questUID);
        JsonObject quest = null;
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        while (questIT.hasNext())
        {
            quest = (JsonObject)questIT.next();
            if (quest.get("uid").getAsString().equals(uuid.toString()))
                return;
        }
        player.getAsJsonArray("quests").add(questTemplate);
    }

    public static void removeQuest(EntityPlayer player, String questUID)
    {
        removeQuest(player.getPersistentID(), questUID);
    }

    public static void removeQuest(UUID uuid, String questUID)
    {
        JsonObject player = getPlayerData(uuid);
        JsonObject quest = null;
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        while (questIT.hasNext())
        {
            quest = (JsonObject)questIT.next();
            if (quest.get("uid").getAsString().equals(uuid.toString()))
            {
                questIT.remove();
                return;
            }
        }
    }

    public static ArrayList<String> getQuestIDs(EntityPlayer player)
    {
        return getQuestIDs(player.getPersistentID());
    }

    public static ArrayList<String> getQuestIDs(UUID playerUUID)
    {
        ArrayList<String> out = new ArrayList<String>();
        JsonObject player = getPlayerData(playerUUID);
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        while (questIT.hasNext())
            out.add(questIT.next().getAsJsonObject().get("uid").getAsString());
        return out;
    }

    public static String[] getQuestArgs(UUID playerUUID, String questUID)
    {
        JsonObject player = getPlayerData(playerUUID);
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        JsonObject quest = null;
        String[] out = null;
        while (questIT.hasNext())
        {
            quest = questIT.next().getAsJsonObject();
            if (quest.get("uid").getAsString().equals(questUID) && quest.getAsJsonArray("args").size() > 0)
            {
                out = new String[quest.getAsJsonArray("args").size()];
                for (int i=0; i<out.length; i++)
                    out[i] = quest.getAsJsonArray("args").get(i).getAsString();
            }
        }
        return out;
    }

    public static void updateQuestArgs(UUID playerUUID, String questUID, String[] args)
    {
        JsonObject player = getPlayerData(playerUUID);
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        JsonObject quest = null;
        while (questIT.hasNext())
        {
            quest = questIT.next().getAsJsonObject();
            if (quest.get("uid").getAsString().equals(questUID))
            {
                quest.remove("args");
                JsonArray newArgs = new JsonArray();
                for (String s : args)
                    newArgs.add(new JsonPrimitive(s));
                quest.add("args", newArgs);
            }
        }
    }
}
