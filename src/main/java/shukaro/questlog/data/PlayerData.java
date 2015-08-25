package shukaro.questlog.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.entity.player.EntityPlayer;
import shukaro.questlog.Questlog;

import java.io.*;
import java.util.Iterator;
import java.util.UUID;

public class PlayerData
{
    private static JsonObject data;
    private static File dataFile;

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

    public static void validate()
    {

    }

    public static void load() throws IOException
    {
        if (!dataFile.exists())
        {
            dataFile.createNewFile();
            data = new JsonObject();
            data.add("players", new JsonArray());
        }
        else
            data = Questlog.gson.fromJson(new BufferedReader(new FileReader(dataFile)), JsonObject.class);
    }

    public static void save() throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(dataFile));
        out.write(data.toString());
        out.close();
    }

    public static void giveQuest(EntityPlayer player, String questUID)
    {
        giveQuest(player.getPersistentID(), questUID);
    }

    public static void giveQuest(UUID uuid, String questUID)
    {
        Iterator<JsonElement> playerIT = data.getAsJsonArray("players").iterator();
        boolean uuidExists = false;
        JsonObject player = null;
        while (playerIT.hasNext())
        {
            player = (JsonObject)playerIT.next();
            if (player.get("uuid").getAsString().equals(uuid.toString()))
            {
                uuidExists = true;
                break;
            }
        }
        if (!uuidExists)
        {
            JsonObject newPlayer = new JsonObject();
            newPlayer.add("uuid", new JsonPrimitive(uuid.toString()));
            newPlayer.add("quests", new JsonArray());
            JsonArray players = data.getAsJsonArray("players");
            players.add(newPlayer);
            player = newPlayer;
        }
        JsonObject questTemplate = QuestData.getQuest(questUID);
        JsonObject quest = null;
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        while (questIT.hasNext())
        {
            quest = (JsonObject)questIT.next();
            if (quest.get("uuid").getAsString().equals(uuid.toString()))
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
        Iterator<JsonElement> playerIT = data.getAsJsonArray("players").iterator();
        boolean uuidExists = false;
        JsonObject player = null;
        while (playerIT.hasNext())
        {
            player = (JsonObject)playerIT.next();
            if (player.get("uuid").getAsString().equals(uuid.toString()))
            {
                uuidExists = true;
                break;
            }
        }
        if (!uuidExists)
        {
            JsonObject newPlayer = new JsonObject();
            newPlayer.add("uuid", new JsonPrimitive(uuid.toString()));
            newPlayer.add("quests", new JsonArray());
            JsonArray players = data.getAsJsonArray("players");
            players.add(newPlayer);
            player = newPlayer;
        }
        JsonObject quest = null;
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        while (questIT.hasNext())
        {
            quest = (JsonObject)questIT.next();
            if (quest.get("uuid").getAsString().equals(uuid.toString()))
            {
                questIT.remove();
                return;
            }
        }
    }
}
