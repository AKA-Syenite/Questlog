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
            data.add("players", new JsonArray());
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

    public void giveQuest(EntityPlayer player, String questUID)
    {
        giveQuest(player.getPersistentID(), questUID);
    }

    public void giveQuest(UUID uuid, String questUID)
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

    public void removeQuest(EntityPlayer player, String questUID)
    {
        removeQuest(player.getPersistentID(), questUID);
    }

    public void removeQuest(UUID uuid, String questUID)
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
