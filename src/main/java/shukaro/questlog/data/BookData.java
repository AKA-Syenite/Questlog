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

public class BookData
{
    private static JsonArray data;
    private static File dataFile;

    private static ResourceLocation templateFile = new ResourceLocation("questlog:templates/bookData.json");
    private static ResourceLocation schemaFile = new ResourceLocation("questlog:schema/bookData.json");

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
            Questlog.logger.warn("Problem accessing book data file");
            e.printStackTrace();
        }
    }

    public static void validate() throws IOException
    {
        Questlog.logger.info("Validating book data");
        JsonNode schema = new JsonNodeReader().fromInputStream(Minecraft.getMinecraft().getResourceManager().getResource(schemaFile).getInputStream());
        JsonNode json = new JsonNodeReader().fromReader(new BufferedReader(new FileReader(dataFile)));
        ProcessingReport report = JsonSchemaFactory.byDefault().getValidator().validateUnchecked(schema, json);
        if (!report.isSuccess())
        {
            Iterator<ProcessingMessage> pit = report.iterator();
            while (pit.hasNext())
                Questlog.logger.warn(pit.next().toString());
            Questlog.logger.warn("Book data json was invalid, aborting");
            data = new JsonArray();
        }
        else
        {
            Questlog.logger.info("Sucessfully validated book data");
            save();
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
            data = Questlog.gson.fromJson(new BufferedReader(new FileReader(dataFile)), JsonObject.class).getAsJsonArray("pages");
    }

    public static void save() throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(dataFile));
        JsonObject temp = new JsonObject();
        temp.add("pages", data);
        out.write(temp.toString());
        out.close();
    }

    public static JsonObject getPage(String uid)
    {
        for (int i=0; i<data.size(); i++)
        {
            if (data.get(i).getAsJsonObject().get("uid").toString().equals("uid"))
                return data.get(i).getAsJsonObject();
        }
        return null;
    }

    public static ArrayList<String> getPages()
    {
        ArrayList<String> out = new ArrayList<String>();
        for (int i=0; i<data.size(); i++)
            out.add(data.get(i).getAsJsonObject().get("uid").getAsString());
        return out;
    }

    public static boolean removePage(String uid)
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

    public static boolean createPage(String uid)
    {
        if (getPage(uid) != null)
            return false;
        else
        {
            JsonObject newPage = new JsonObject();
            newPage.add("uid", new JsonPrimitive(uid));
            newPage.add("questNodes", new JsonArray());
            newPage.add("lineNodes", new JsonArray());
            newPage.add("pageNodes", new JsonArray());
            data.add(newPage);
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
    }

    public static boolean createQuestNode(String pageUID, String nodeUID, String questUID, int x, int y, String[] parents, String[] tags)
    {
        if (getPage(pageUID) == null || QuestData.getQuest(questUID) == null || getNodeOnPage(getPage(pageUID), nodeUID) != null)
            return false;
        else
        {
            JsonObject node = new JsonObject();
            node.add("uid", new JsonPrimitive(nodeUID));
            node.add("questUID", new JsonPrimitive(questUID));
            node.add("x", new JsonPrimitive(x));
            node.add("y", new JsonPrimitive(y));
            node.add("parents", Questlog.parser.parse(Questlog.gson.toJson(parents)).getAsJsonArray());
            node.add("tags", Questlog.parser.parse(Questlog.gson.toJson(tags)).getAsJsonArray());
            getPage(pageUID).getAsJsonArray("questNodes").add(node);
            return true;
        }
    }

    public static boolean createPageNode(String pageUID, String nodeUID, String targetUID, int x, int y, String[] parents, String[] tags)
    {
        if (getPage(pageUID) == null || getPage(targetUID) == null || getNodeOnPage(getPage(pageUID), nodeUID) != null)
            return false;
        else
        {
            JsonObject node = new JsonObject();
            node.add("uid", new JsonPrimitive(nodeUID));
            node.add("target", new JsonPrimitive(targetUID));
            node.add("x", new JsonPrimitive(x));
            node.add("y", new JsonPrimitive(y));
            node.add("parents", Questlog.parser.parse(Questlog.gson.toJson(parents)).getAsJsonArray());
            node.add("tags", Questlog.parser.parse(Questlog.gson.toJson(tags)).getAsJsonArray());
            getPage(pageUID).getAsJsonArray("pageNodes").add(node);
            return true;
        }
    }

    public static boolean createLineNode(String pageUID, String nodeUID, int x, int y, int x2, int y2, String[] parents, String[] tags)
    {
        if (getPage(pageUID) == null || getNodeOnPage(getPage(pageUID), nodeUID) != null)
            return false;
        else
        {
            JsonObject node = new JsonObject();
            node.add("uid", new JsonPrimitive(nodeUID));
            node.add("x", new JsonPrimitive(x));
            node.add("y", new JsonPrimitive(y));
            node.add("x2", new JsonPrimitive(x2));
            node.add("y2", new JsonPrimitive(y2));
            node.add("parents", Questlog.parser.parse(Questlog.gson.toJson(parents)).getAsJsonArray());
            node.add("tags", Questlog.parser.parse(Questlog.gson.toJson(tags)).getAsJsonArray());
            getPage(pageUID).getAsJsonArray("lineNodes").add(node);
            return true;
        }
    }

    public static boolean removeQuestNode(String pageUID, String nodeUID)
    {
        JsonObject page = getPage(pageUID);
        if (page == null || getNodeOnPage(pageUID, nodeUID) == null)
            return false;
        JsonArray newNodes = new JsonArray();
        for (JsonElement e : page.getAsJsonArray("questNodes"))
        {
            JsonObject node = (JsonObject)e;
            if (!node.get("uid").getAsString().equals(nodeUID))
                newNodes.add(node);
        }
        page.remove("questNodes");
        page.add("questNodes", newNodes);
        return true;
    }

    public static boolean removePageNode(String pageUID, String nodeUID)
    {
        JsonObject page = getPage(pageUID);
        if (page == null || getNodeOnPage(pageUID, nodeUID) == null)
            return false;
        JsonArray newNodes = new JsonArray();
        for (JsonElement e : page.getAsJsonArray("pageNodes"))
        {
            JsonObject node = (JsonObject)e;
            if (!node.get("uid").getAsString().equals(nodeUID))
                newNodes.add(node);
        }
        page.remove("pageNodes");
        page.add("pageNodes", newNodes);
        return true;
    }

    public static boolean removeLineNode(String pageUID, String nodeUID)
    {
        JsonObject page = getPage(pageUID);
        if (page == null || getNodeOnPage(pageUID, nodeUID) == null)
            return false;
        JsonArray newNodes = new JsonArray();
        for (JsonElement e : page.getAsJsonArray("lineNodes"))
        {
            JsonObject node = (JsonObject)e;
            if (!node.get("uid").getAsString().equals(nodeUID))
                newNodes.add(node);
        }
        page.remove("lineNodes");
        page.add("lineNodes", newNodes);
        return true;
    }

    protected static JsonArray getNodesOnPage(JsonObject page)
    {
        JsonArray temp = new JsonArray();
        temp.addAll(page.getAsJsonArray("questNodes"));
        temp.addAll(page.getAsJsonArray("lineNodes"));
        temp.addAll(page.getAsJsonArray("pageNodes"));
        return temp;
    }

    public static ArrayList<String> getNodeIDsOnPage(String pageUID)
    {
        JsonObject node = getPage(pageUID);
        ArrayList<String> out = new ArrayList<String>();
        for (JsonElement e : getNodesOnPage(node))
            out.add(((JsonObject)e).get("uid").getAsString());
        return out;
    }

    protected static JsonObject getNodeOnPage(JsonObject page, String uid)
    {
        for (JsonElement node : getNodesOnPage(page))
        {
            if (node.getAsJsonObject().get("uid").getAsString().equals(uid))
                return node.getAsJsonObject();
        }
        return null;
    }

    public static JsonObject getNodeOnPage(String pageUID, String uid)
    {
        return getNodeOnPage(getPage(pageUID), uid);
    }

    public static ArrayList<String> getNodeParents(String pageUID, String nodeUID)
    {
        JsonObject node = getNodeOnPage(getPage(pageUID), nodeUID);
        ArrayList<String> out = new ArrayList<String>();
        if (node != null)
        {
            JsonArray parents = node.get("parents").getAsJsonArray();
            for (JsonElement e : parents)
                out.add(e.getAsString());
        }
        return out;
    }

    public static ArrayList<String> getNodeTags(String pageUID, String nodeUID)
    {
        JsonObject node = getNodeOnPage(getPage(pageUID), nodeUID);
        ArrayList<String> out = new ArrayList<String>();
        if (node != null)
        {
            JsonArray tags = node.get("tags").getAsJsonArray();
            for (JsonElement e : tags)
                out.add(e.getAsString());
        }
        return out;
    }

    public static int[] getNodePos(String pageUID, String nodeUID)
    {
        JsonObject node = getNodeOnPage(getPage(pageUID), nodeUID);
        int[] pos = new int[2];
        if (node != null)
        {
            pos[0] = node.get("x").getAsInt();
            pos[1] = node.get("y").getAsInt();
        }
        return pos;
    }

    public static String getQuestUIDForQuestNode(String pageUID, String nodeUID)
    {
        JsonObject node = getNodeOnPage(getPage(pageUID), nodeUID);
        if (node != null && node.has("questUID"))
            return node.get("questUID").getAsString();
        return "";
    }

    public static int[] getSecondaryPosForLineNode(String pageUID, String nodeUID)
    {
        JsonObject node = getNodeOnPage(getPage(pageUID), nodeUID);
        int[] pos = new int[2];
        if (node != null && node.has("x2"))
        {
            pos[0] = node.get("x2").getAsInt();
            pos[1] = node.get("y2").getAsInt();
        }
        return pos;
    }

    public static String getTargetForPageNode(String pageUID, String nodeUID)
    {
        JsonObject node = getNodeOnPage(getPage(pageUID), nodeUID);
        if (node != null && node.has("target"))
            return node.get("target").getAsString();
        return "";
    }
}