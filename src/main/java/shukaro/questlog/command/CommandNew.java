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
                String[] objectives = args[3].split(";");
                String[] rewards = args[4].split(";");
                String[] tags = args[5].split(";");
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

            }
            else if (target.equals("pageNode") && args.length == 10)
            {

            }
            else if (target.equals("lineNode") && args.length == 9)
            {

            }
        }
        else
            throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, "quest", "page", "questNode", "pageNode", "lineNode");
        return null;
    }
}
