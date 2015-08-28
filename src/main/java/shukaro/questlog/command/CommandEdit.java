package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.StringHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import shukaro.questlog.Questlog;
import shukaro.questlog.data.BookData;
import shukaro.questlog.data.QuestData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CommandEdit implements ISubCommand
{
    public static final List<String> questTargets = Arrays.asList("uid", "objectives", "rewards", "tags");
    public static final List<String> pageTargets = Arrays.asList("uid");
    public static final List<String> questNodeTargets = Arrays.asList("pageUID", "nodeUID", "questUID", "x", "y", "parents", "tags");
    public static final List<String> pageNodeTargets = Arrays.asList("pageUID", "nodeUID", "targetUID", "x", "y", "parents", "tags");
    public static final List<String> lineNodeTargets = Arrays.asList("pageUID", "nodeUID", "x", "y", "x2", "y2", "parents", "tags");

    @Override
    public int getPermissionLevel()
    {
        return 3;
    }

    @Override
    public String getCommandName()
    {
        return "edit";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] args)
    {
        doHandle(sender, args);
        if (args.length > 2)
        {
            try
            {
                if (args[1].equals("quest"))
                    QuestData.save();
                else
                    BookData.save();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void doHandle(ICommandSender sender, String[] args)
    {
        if (args[1].equals("quest"))
        {
            if (args.length != 5)
                throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            if (args[2].equals("uid"))
            {
                if (QuestData.getQuest(args[3]) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid")));
                    return;
                }
                QuestData.setQuestUID(args[3], args[4]);
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatedquest")));
            }
            else if (args[2].equals("objectives"))
            {
                if (QuestData.getQuest(args[3]) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid")));
                    return;
                }
                QuestData.setQuestObjectives(args[3], args[4].split(";"));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatedquest")));
            }
            else if (args[2].equals("rewards"))
            {
                if (QuestData.getQuest(args[3]) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid")));
                    return;
                }
                QuestData.setQuestRewards(args[3], args[4].split(";"));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatedquest")));
            }
            else if (args[2].equals("tags"))
            {
                if (QuestData.getQuest(args[3]) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid")));
                    return;
                }
                QuestData.setQuestTags(args[3], args[4].split(";"));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatedquest")));
            }
            else
                throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
        }
        else if (args[1].equals("page"))
        {
            if (args.length != 5)
                throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            if (args[2].equals("uid"))
            {
                if (BookData.getPage(args[3]) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid")));
                    return;
                }
                BookData.setPageUID(args[3], args[4]);
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatedpage")));
            }
            else
                throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
        }
        else if (args[1].equals("questNode"))
        {
            if (args.length != 6)
                throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            if (args[2].equals("pageUID"))
            {
                String pageUID = args[3];
                String nodeUID = args[4];
                String newPageUID = args[5];
                JsonObject node = BookData.getNodeOnPage(pageUID, nodeUID);
                BookData.removeQuestNode(pageUID, nodeUID);
                BookData.createQuestNode(newPageUID, nodeUID, node.get("questUID").getAsString(),
                        Integer.parseInt(node.get("x").getAsString()), Integer.parseInt(node.get("x").getAsString()),
                        node.getAsJsonArray("parents").toString().replace("},{", " ,").split(" "),
                        node.getAsJsonArray("tags").toString().replace("},{", " ,").split(" "));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("nodeUID"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newNodeUID = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("uid");
                node.add("uid", new JsonPrimitive(newNodeUID));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("questUID"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newQuestUID = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("questUID");
                node.add("questUID", new JsonPrimitive(newQuestUID));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("x"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newX = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("x");
                node.add("x", new JsonPrimitive(newX));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("y"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newY = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("y");
                node.add("y", new JsonPrimitive(newY));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("parents"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String[] newParents = args[5].split(";");
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("parents");
                node.add("parents", Questlog.parser.parse(Questlog.gson.toJson(newParents)).getAsJsonArray());
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("tags"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String[] newTags = args[5].split(";");
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("tags");
                node.add("tags", Questlog.parser.parse(Questlog.gson.toJson(newTags)).getAsJsonArray());
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else
                throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
        }
        else if (args[1].equals("pageNode"))
        {
            if (args.length != 6)
                throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            if (args[2].equals("pageUID"))
            {
                String pageUID = args[3];
                String nodeUID = args[4];
                String newPageUID = args[5];
                JsonObject node = BookData.getNodeOnPage(pageUID, nodeUID);
                BookData.removeQuestNode(pageUID, nodeUID);
                BookData.createQuestNode(newPageUID, nodeUID, node.get("questUID").getAsString(),
                        Integer.parseInt(node.get("x").getAsString()), Integer.parseInt(node.get("x").getAsString()),
                        node.getAsJsonArray("parents").toString().replace("},{", " ,").split(" "),
                        node.getAsJsonArray("tags").toString().replace("},{", " ,").split(" "));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("nodeUID"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newNodeUID = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("uid");
                node.add("uid", new JsonPrimitive(newNodeUID));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("targetUID"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newTargetUID = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("targetUID");
                node.add("targetUID", new JsonPrimitive(newTargetUID));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("x"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newX = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("x");
                node.add("x", new JsonPrimitive(newX));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("y"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newY = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("y");
                node.add("y", new JsonPrimitive(newY));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("parents"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String[] newParents = args[5].split(";");
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("parents");
                node.add("parents", Questlog.parser.parse(Questlog.gson.toJson(newParents)).getAsJsonArray());
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("tags"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String[] newTags = args[5].split(";");
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("tags");
                node.add("tags", Questlog.parser.parse(Questlog.gson.toJson(newTags)).getAsJsonArray());
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else
                throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
        }
        else if (args[1].equals("lineNode"))
        {
            if (args.length != 6)
                throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            if (args[2].equals("pageUID"))
            {
                String pageUID = args[3];
                String nodeUID = args[4];
                String newPageUID = args[5];
                JsonObject node = BookData.getNodeOnPage(pageUID, nodeUID);
                BookData.removeQuestNode(pageUID, nodeUID);
                BookData.createQuestNode(newPageUID, nodeUID, node.get("questUID").getAsString(),
                        Integer.parseInt(node.get("x").getAsString()), Integer.parseInt(node.get("x").getAsString()),
                        node.getAsJsonArray("parents").toString().replace("},{", " ,").split(" "),
                        node.getAsJsonArray("tags").toString().replace("},{", " ,").split(" "));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("nodeUID"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newNodeUID = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("uid");
                node.add("uid", new JsonPrimitive(newNodeUID));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("x"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newX = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("x");
                node.add("x", new JsonPrimitive(newX));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("y"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newY = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("y");
                node.add("y", new JsonPrimitive(newY));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("x2"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newX2 = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("x2");
                node.add("x2", new JsonPrimitive(newX2));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("y2"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String newY2 = args[5];
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("y2");
                node.add("y2", new JsonPrimitive(newY2));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("parents"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String[] newParents = args[5].split(";");
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("parents");
                node.add("parents", Questlog.parser.parse(Questlog.gson.toJson(newParents)).getAsJsonArray());
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (args[2].equals("tags"))
            {
                String homePage = args[3];
                String nodeUID = args[4];
                String[] newTags = args[5].split(";");
                JsonObject node = BookData.getNodeOnPage(homePage, nodeUID);
                node.remove("tags");
                node.add("tags", Questlog.parser.parse(Questlog.gson.toJson(newTags)).getAsJsonArray());
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.updatednode")));
            }
            else if (CommandHandler.targets.contains(args[1]))
                throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            else
                throw new WrongUsageException("command.questlog." + getCommandName() + ".syntax");
        }
        else
            throw new WrongUsageException("command.questlog." + getCommandName() + ".syntax");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandHandler.targets.toArray(new String[CommandHandler.targets.size()]));
        else if (args.length == 3 && CommandHandler.targets.contains(args[1]))
        {
            if (args[1].equals("quest"))
                return CommandBase.getListOfStringsMatchingLastWord(args, questTargets.toArray(new String[questTargets.size()]));
            else if (args[1].equals("page"))
                return CommandBase.getListOfStringsMatchingLastWord(args, pageTargets.toArray(new String[pageTargets.size()]));
            else if (args[1].equals("questNode"))
                return CommandBase.getListOfStringsMatchingLastWord(args, questNodeTargets.toArray(new String[questNodeTargets.size()]));
            else if (args[1].equals("pageNode"))
                return CommandBase.getListOfStringsMatchingLastWord(args, pageNodeTargets.toArray(new String[pageNodeTargets.size()]));
            else if (args[1].equals("lineNode"))
                return CommandBase.getListOfStringsMatchingLastWord(args, lineNodeTargets.toArray(new String[lineNodeTargets.size()]));
        }
        return null;
    }
}
