package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import shukaro.questlog.data.BookData;
import shukaro.questlog.data.QuestData;

import java.util.List;

public class CommandGet implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 1;
    }

    @Override
    public String getCommandName()
    {
        return "get";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] args)
    {
        if (args.length > 1)
        {
            String target = args[1];
            if (target.equals("quest") && args.length == 3)
            {
                String questUID = args[2];
                if (QuestData.getQuest(questUID) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid")));
                    return;
                }
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.uid") + " " + StringHelper.YELLOW + questUID + StringHelper.END));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.objectives") + " " + StringHelper.YELLOW + QuestData.getQuestObjectives(questUID).toString() + StringHelper.END));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.rewards") + " " + StringHelper.YELLOW + QuestData.getQuestRewards(questUID).toString() + StringHelper.END));
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.tags") + " " + StringHelper.YELLOW + QuestData.getQuestTags(questUID).toString() + StringHelper.END));
            }
            else if (target.equals("page") && args.length == 3)
            {
                String pageUID = args[2];
                if (BookData.getPage(pageUID) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid")));
                    return;
                }
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.uid") + " " + StringHelper.YELLOW + pageUID + StringHelper.END));
                StringBuilder qnOut = new StringBuilder(StringHelper.localize("command.questlog.questnodes") + " ");
                for (String s : BookData.getQuestNodeIDsOnPage(pageUID))
                    qnOut.append(StringHelper.YELLOW + s + StringHelper.END + ", ");
                if (BookData.getQuestNodeIDsOnPage(pageUID).size() > 2)
                    qnOut.delete(qnOut.length()-2, qnOut.length());
                StringBuilder pnOut = new StringBuilder(StringHelper.localize("command.questlog.pagenodes") + " ");
                for (String s : BookData.getPageNodeIDsOnPage(pageUID))
                    pnOut.append(StringHelper.YELLOW + s + StringHelper.END + ", ");
                if (BookData.getPageNodeIDsOnPage(pageUID).size() > 2)
                    pnOut.delete(pnOut.length()-2, pnOut.length());
                StringBuilder lnOut = new StringBuilder(StringHelper.localize("command.questlog.linenodes") + " ");
                for (String s : BookData.getLineNodeIDsOnPage(pageUID))
                    lnOut.append(StringHelper.YELLOW + s + StringHelper.END + ", ");
                if (BookData.getLineNodeIDsOnPage(pageUID).size() > 2)
                    lnOut.delete(lnOut.length()-2, lnOut.length());
                sender.addChatMessage(new ChatComponentText(qnOut.toString()));
                sender.addChatMessage(new ChatComponentText(pnOut.toString()));
                sender.addChatMessage(new ChatComponentText(lnOut.toString()));
            }
            else if (target.equals("questNode") && args.length == 4)
            {
                String pageUID = args[2];
                String nodeUID = args[3];
                if (BookData.getPage(pageUID) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid") + " " + StringHelper.localize("command.questlog.first")));
                    return;
                }
                else if (BookData.getNodeOnPage(pageUID, nodeUID) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid") + " " + StringHelper.localize("command.questlog.second")));
                    return;
                }
                else
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.uid") + " " + nodeUID));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.questuid") + " " + BookData.getQuestUIDForQuestNode(pageUID, nodeUID)));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.x") + " " + BookData.getNodePos(pageUID, nodeUID)[0]));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.y") + " " + BookData.getNodePos(pageUID, nodeUID)[1]));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.parents") + " " + BookData.getNodeParents(pageUID, nodeUID)));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.tags") + " " + BookData.getNodeTags(pageUID, nodeUID)));
                }
            }
            else if (target.equals("pageNode") && args.length == 4)
            {
                String pageUID = args[2];
                String nodeUID = args[3];
                if (BookData.getPage(pageUID) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid") + " " + StringHelper.localize("command.questlog.first")));
                    return;
                }
                else if (BookData.getNodeOnPage(pageUID, nodeUID) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid") + " " + StringHelper.localize("command.questlog.second")));
                    return;
                }
                else
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.uid") + " " + nodeUID));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.target") + " " + BookData.getTargetForPageNode(pageUID, nodeUID)));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.x") + " " + BookData.getNodePos(pageUID, nodeUID)[0]));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.y") + " " + BookData.getNodePos(pageUID, nodeUID)[1]));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.parents") + " " + BookData.getNodeParents(pageUID, nodeUID)));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.tags") + " " + BookData.getNodeTags(pageUID, nodeUID)));
                }
            }
            else if (target.equals("lineNode") && args.length == 4)
            {
                String pageUID = args[2];
                String nodeUID = args[3];
                if (BookData.getPage(pageUID) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid") + " " + StringHelper.localize("command.questlog.first")));
                    return;
                }
                else if (BookData.getNodeOnPage(pageUID, nodeUID) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid") + " " + StringHelper.localize("command.questlog.second")));
                    return;
                }
                else
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.uid") + " " + nodeUID));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.x") + " " + BookData.getNodePos(pageUID, nodeUID)[0]));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.y") + " " + BookData.getNodePos(pageUID, nodeUID)[1]));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.x2") + " " + BookData.getSecondaryPosForLineNode(pageUID, nodeUID)[0]));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.y2") + " " + BookData.getSecondaryPosForLineNode(pageUID, nodeUID)[1]));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.parents") + " " + BookData.getNodeParents(pageUID, nodeUID)));
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.tags") + " " + BookData.getNodeTags(pageUID, nodeUID)));
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
