package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import shukaro.questlog.data.BookData;
import shukaro.questlog.data.QuestData;

import java.util.List;

public class CommandNew implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 3;
    }

    @Override
    public String getCommandName()
    {
        return "new";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] args)
    {
        if (args.length > 2)
        {
            String target = args[1];
            if (target.equals("quest") && args.length == 6)
            {
                String questUID = args[2];
                String[] objectives = CommandHandler.nullSynonyms.contains(args[3]) ? new String[]{""} : args[3].split(";");
                String[] rewards = CommandHandler.nullSynonyms.contains(args[4]) ? new String[]{""} : args[4].split(";");
                String[] tags = CommandHandler.nullSynonyms.contains(args[5]) ? new String[]{""} : args[5].split(";");
                if (QuestData.getQuest(questUID) != null)
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.uidtaken")));
                else
                {
                    QuestData.createQuest(questUID, objectives, rewards, tags);
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.questcreated")));
                    if (ServerHelper.isMultiPlayerServer())
                    {
                        //sync to other clients
                    }
                }
            }
            else if (target.equals("page") && args.length == 3)
            {
                String pageUID = args[2];
                if (BookData.createPage(pageUID))
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.pagecreated")));
                    if (ServerHelper.isMultiPlayerServer())
                    {
                        //sync to other clients
                    }
                }
                else
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.uidtaken")));
            }
            else if (target.equals("questNode") && args.length == 9)
            {
                String pageUID = args[2];
                String nodeUID = args[3];
                String questUID = args[4];
                String xs = args[5];
                String ys = args[6];
                String[] parents = CommandHandler.nullSynonyms.contains(args[7]) ? new String[]{""} : args[7].split(";");
                String[] tags = CommandHandler.nullSynonyms.contains(args[8]) ? new String[]{""} : args[8].split(";");
                int x;
                int y;
                try
                {
                    x = Integer.parseInt(xs);
                    y = Integer.parseInt(ys);
                }
                catch (NumberFormatException e)
                {
                    throw new WrongUsageException("command.questlog.coordnotint");
                }
                if (BookData.getPage(pageUID) == null)
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchpage")));
                else if (QuestData.getQuest(questUID) == null)
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchquest")));
                else if (BookData.getNodeOnPage(pageUID, nodeUID) != null)
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.uidtaken")));
                else
                {
                    BookData.createQuestNode(pageUID, nodeUID, questUID, x, y, parents, tags);
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.questnodecreated")));
                    if (ServerHelper.isMultiPlayerServer())
                    {
                        //sync to other clients
                    }
                }
            }
            else if (target.equals("pageNode") && args.length == 9)
            {
                String pageUID = args[2];
                String nodeUID = args[3];
                String targetUID = args[4];
                String xs = args[5];
                String ys = args[6];
                String[] parents = CommandHandler.nullSynonyms.contains(args[7]) ? new String[]{""} : args[7].split(";");
                String[] tags = CommandHandler.nullSynonyms.contains(args[8]) ? new String[]{""} : args[8].split(";");
                int x;
                int y;
                try
                {
                    x = Integer.parseInt(xs);
                    y = Integer.parseInt(ys);
                }
                catch (NumberFormatException e)
                {
                    throw new WrongUsageException("command.questlog.coordnotint");
                }
                if (BookData.getPage(pageUID) == null)
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchpage") + " " + StringHelper.localize("command.questlog.first")));
                else if (BookData.getPage(targetUID) == null)
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchpage") + " " + StringHelper.localize("command.questlog.second")));
                else if (BookData.getNodeOnPage(pageUID, nodeUID) != null)
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.uidtaken")));
                else
                {
                    BookData.createPageNode(pageUID, nodeUID, targetUID, x, y, parents, tags);
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.pagenodecreated")));
                    if (ServerHelper.isMultiPlayerServer())
                    {
                        //sync to other clients
                    }
                }
            }
            else if (target.equals("lineNode") && args.length == 10)
            {
                String pageUID = args[2];
                String nodeUID = args[3];
                String xs = args[4];
                String ys = args[5];
                String x2s = args[6];
                String y2s = args[7];
                String[] parents = CommandHandler.nullSynonyms.contains(args[8]) ? new String[]{""} : args[8].split(";");
                String[] tags = CommandHandler.nullSynonyms.contains(args[9]) ? new String[]{""} : args[9].split(";");
                int x;
                int y;
                int x2;
                int y2;
                try
                {
                    x = Integer.parseInt(xs);
                    y = Integer.parseInt(ys);
                    x2 = Integer.parseInt(x2s);
                    y2 = Integer.parseInt(y2s);
                }
                catch (NumberFormatException e)
                {
                    throw new WrongUsageException("command.questlog.coordnotint");
                }
                if (BookData.getPage(pageUID) == null)
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchpage")));
                else if (BookData.getNodeOnPage(pageUID, nodeUID) != null)
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.uidtaken")));
                else
                {
                    BookData.createLineNode(pageUID, nodeUID, x, y, x2, y2, parents, tags);
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.linenodecreated")));
                    if (ServerHelper.isMultiPlayerServer())
                    {
                        //sync to other clients
                    }
                }
            }
            else if (CommandHandler.targets.contains(target))
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
        return null;
    }
}
