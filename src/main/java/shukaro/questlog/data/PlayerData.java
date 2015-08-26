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

    public static void init(File file)
    {
        dataFile = file;
        try
        {
            load();
            validate();
            instantiateAllObjectives();
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
                updateObjectives(entry.getKey(), obj.parentQuest, obj.saveToStringArray());
        }
        jo.add("players", data);
        out.write(jo.toString());
        out.close();
    }

    public static void instantiateAllObjectives()
    {
        Iterator<JsonElement> playerIT = data.iterator();
        while (playerIT.hasNext())
        {
            JsonObject player = (JsonObject)playerIT.next();
            UUID playerUUID = UUID.fromString(player.get("uuid").getAsString());
            for (String questUID : getQuestIDs(playerUUID))
                QuestManager.instantiateObjectives(playerUUID, questUID);
        }
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
            newPlayer.add("groups", new JsonArray());
            newPlayer.add("leading", new JsonArray());
            newPlayer.add("quests", new JsonArray());
            data.add(newPlayer);
            player = newPlayer;
        }
        return player;
    }

    public static void giveQuest(UUID uuid, String questUID)
    {
        JsonObject player = getPlayerData(uuid);
        JsonObject quest = null;
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        while (questIT.hasNext())
        {
            quest = (JsonObject)questIT.next();
            if (quest.get("uid").getAsString().equals(uuid.toString()))
                return;
        }
        JsonObject newQuest = new JsonObject();
        newQuest.add("uid", new JsonPrimitive(questUID));
        newQuest.add("complete", new JsonPrimitive("false"));
        newQuest.add("tracked", new JsonPrimitive("false"));
        newQuest.add("objectives", new JsonArray());
        player.getAsJsonArray("quests").add(newQuest);
        QuestManager.instantiateObjectives(uuid, questUID);
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

    public static ArrayList<String> getQuestIDs(UUID playerUUID)
    {
        ArrayList<String> out = new ArrayList<String>();
        JsonObject player = getPlayerData(playerUUID);
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        while (questIT.hasNext())
            out.add(questIT.next().getAsJsonObject().get("uid").getAsString());
        return out;
    }

    public static boolean playerHasQuest(UUID playerUUID, String questUID)
    {
        return getQuestIDs(playerUUID).contains(questUID);
    }

    public static String[] getObjectives(UUID playerUUID, String questUID)
    {
        JsonObject player = getPlayerData(playerUUID);
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        JsonObject quest = null;
        String[] out = null;
        while (questIT.hasNext())
        {
            quest = questIT.next().getAsJsonObject();
            if (quest.get("uid").getAsString().equals(questUID) && quest.getAsJsonArray("objectives").size() > 0)
            {
                out = new String[quest.getAsJsonArray("objectives").size()];
                for (int i=0; i<out.length; i++)
                    out[i] = quest.getAsJsonArray("objectives").get(i).getAsString();
            }
        }
        return out;
    }

    public static void updateObjectives(UUID playerUUID, String questUID, String[] objectives)
    {
        JsonObject player = getPlayerData(playerUUID);
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        JsonObject quest = null;
        while (questIT.hasNext())
        {
            quest = questIT.next().getAsJsonObject();
            if (quest.get("uid").getAsString().equals(questUID))
            {
                quest.remove("objectives");
                JsonArray newArgs = new JsonArray();
                for (String s : objectives)
                    newArgs.add(new JsonPrimitive(s));
                quest.add("objectives", newArgs);
            }
        }
    }

    public static boolean isTrackingQuest(UUID playerUUID, String questUID)
    {
        JsonObject player = getPlayerData(playerUUID);
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        JsonObject quest = null;
        while (questIT.hasNext())
        {
            quest = questIT.next().getAsJsonObject();
            if (quest.get("uid").getAsString().equals(questUID))
                return quest.get("tracked").getAsString().equals("true");
        }
        return false;
    }

    public static ArrayList<String> getTrackedQuests(UUID playerUUID)
    {
        ArrayList<String> tracked = new ArrayList<String>();
        for (String questUID : getQuestIDs(playerUUID))
        {
            if (isTrackingQuest(playerUUID, questUID))
                tracked.add(questUID);
        }
        return tracked;
    }

    public static void setTrackingQuest(UUID playerUUID, String questUID, boolean tracked)
    {
        if (playerHasQuest(playerUUID, questUID))
        {
            JsonObject player = getPlayerData(playerUUID);
            Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
            JsonObject quest = null;
            while (questIT.hasNext())
            {
                quest = questIT.next().getAsJsonObject();
                if (quest.get("uid").getAsString().equals(questUID))
                {
                    quest.remove("tracked");
                    quest.add("tracked", new JsonPrimitive(tracked));
                }
            }
        }
    }

    public static boolean getQuestCompletion(UUID playerUUID, String questUID)
    {
        JsonObject player = getPlayerData(playerUUID);
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        JsonObject quest = null;
        while (questIT.hasNext())
        {
            quest = questIT.next().getAsJsonObject();
            if (quest.get("uid").getAsString().equals(questUID))
                return quest.get("complete").getAsString().equals("true");
        }
        return false;
    }

    public static void updateQuestCompletion(UUID playerUUID, String questUID, boolean complete)
    {
        JsonObject player = getPlayerData(playerUUID);
        Iterator<JsonElement> questIT = player.getAsJsonArray("quests").iterator();
        JsonObject quest = null;
        while (questIT.hasNext())
        {
            quest = questIT.next().getAsJsonObject();
            if (quest.get("uid").getAsString().equals(questUID))
            {
                quest.remove("complete");
                quest.add("complete", new JsonPrimitive(complete ? "true" : "false"));
            }
        }
    }

    public static String[] getPlayerGroups(UUID playerUUID)
    {
        JsonObject player = getPlayerData(playerUUID);
        String[] out = null;
        int numGroups = player.getAsJsonArray("groups").size() + player.getAsJsonArray("leading").size();
        if (numGroups > 0)
        {
            out = new String[numGroups];
            int i = 0;
            for (JsonElement e : player.getAsJsonArray("groups"))
            {
                out[i] = player.getAsJsonArray("groups").get(i).getAsString();
                i++;
            }
            for (JsonElement e : player.getAsJsonArray("leading"))
            {
                out[i] = player.getAsJsonArray("leading").get(i).getAsString();
                i++;
            }
        }
        return out;
    }

    public static UUID[] getPlayersInGroup(String groupUID)
    {
        Iterator<JsonElement> playerIT = data.iterator();
        ArrayList<UUID> players = new ArrayList<UUID>();
        while (playerIT.hasNext())
        {
            JsonObject player = (JsonObject)playerIT.next();
            UUID playerUUID = UUID.fromString(player.get("uuid").getAsString());
            if (isPlayerInGroup(playerUUID, groupUID))
                players.add(playerUUID);
        }
        return players.toArray(new UUID[players.size()]);
    }

    public static boolean isPlayerInGroup(UUID playerUUID, String groupUID)
    {
        String[] groups = getPlayerGroups(playerUUID);
        for (String s : groups)
        {
            if (s.equals(groupUID))
                return true;
        }
        return false;
    }

    public static boolean isPlayerLeading(UUID playerUUID, String groupUID)
    {
        if (isPlayerInGroup(playerUUID, groupUID))
        {
            JsonObject player = getPlayerData(playerUUID);
            for (JsonElement g : player.getAsJsonArray("leading"))
            {
                if (g.getAsString().equals(groupUID))
                    return true;
            }
        }
        return false;
    }

    public static UUID getGroupLeader(String groupUID)
    {
        for (UUID player : getPlayersInGroup(groupUID))
        {
            if (isPlayerLeading(player, groupUID))
                return player;
        }
        return null;
    }

    public static void setGroupLeader(UUID playerUUID, String groupUID)
    {
        for (UUID player : getPlayersInGroup(groupUID))
        {
            if (isPlayerLeading(player, groupUID))
            {
                removePlayerFromGroup(player, groupUID);
                addPlayerToGroup(player, groupUID);
            }
        }
        JsonObject player = getPlayerData(playerUUID);
        player.getAsJsonArray("leading").add(new JsonPrimitive(groupUID));
    }

    public static void addPlayerToGroup(UUID playerUUID, String groupUID)
    {
        if (!isPlayerInGroup(playerUUID, groupUID))
        {
            JsonObject player = getPlayerData(playerUUID);
            player.getAsJsonArray("groups").add(new JsonPrimitive(groupUID));
        }
    }

    public static void removePlayerFromGroup(UUID playerUUID, String groupUID)
    {
        if (isPlayerInGroup(playerUUID, groupUID))
        {
            if (isPlayerLeading(playerUUID, groupUID))
            {
                JsonObject player = getPlayerData(playerUUID);
                JsonArray leading = player.getAsJsonArray("leading");
                String[] newLeading = new String[leading.size()-1];
                int i = 0;
                for (JsonElement e : leading)
                {
                    String s = e.getAsString();
                    if (!s.equals(groupUID))
                    {
                        newLeading[i] = s;
                        i++;
                    }
                }
                player.remove("leading");
                player.add("leading", Questlog.parser.parse(Questlog.gson.toJson(newLeading)).getAsJsonArray());
            }
            else
            {
                JsonObject player = getPlayerData(playerUUID);
                JsonArray groups = player.getAsJsonArray("groups");
                String[] newGroups = new String[groups.size()-1];
                int i = 0;
                for (JsonElement e : groups)
                {
                    String s = e.getAsString();
                    if (!s.equals(groupUID))
                    {
                        newGroups[i] = s;
                        i++;
                    }
                }
                player.remove("groups");
                player.add("groups", Questlog.parser.parse(Questlog.gson.toJson(newGroups)).getAsJsonArray());
            }
        }
    }
}
