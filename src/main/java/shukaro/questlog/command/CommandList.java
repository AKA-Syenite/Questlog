package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import shukaro.questlog.data.BookData;
import shukaro.questlog.data.QuestData;

import java.util.Arrays;
import java.util.List;

public class CommandList implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandName()
    {
        return "list";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] args)
    {
        if (args.length > 1)
        {
            String target = args[1];
            Iterable<String> idList = null;
            if (CommandHandler.targets.contains(target) && args.length == 3)
            {
                if (BookData.getPage(args[2]) == null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid")));
                    return;
                }
            }

            if (target.equals("quests") && args.length == 2)
                idList = QuestData.getQuestIDs();
            else if (target.equals("pages") && args.length == 2)
                idList = BookData.getPageIDs();
            else if (target.equals("questNodes") && args.length == 3)
                idList = BookData.getQuestNodeIDsOnPage(args[2]);
            else if (target.equals("pageNodes") && args.length == 3)
                idList = BookData.getPageNodeIDsOnPage(args[2]);
            else if (target.equals("lineNodes") && args.length == 3)
                idList = BookData.getLineNodeIDsOnPage(args[2]);

            if (idList != null)
            {
                StringBuilder out = new StringBuilder("");
                String pattern = "";
                if (args.length == 3)
                    pattern = args[2];
                int i = 0;
                for (String id : idList)
                {
                    if (pattern.length() > 0 && out.toString().matches(pattern))
                    {
                        out.append(id + ", ");
                        i++;
                    }
                    else if (pattern.length() == 0)
                    {
                        out.append(id + ", ");
                        i++;
                    }
                }
                if (out.length() > 2)
                {
                    out.delete(out.length() - 2, out.length());
                    sender.addChatMessage(new ChatComponentText(StringHelper.LIGHT_BLUE + out));
                }
                sender.addChatMessage(new ChatComponentText(StringHelper.WHITE + StringHelper.localize("command.questlog.total") + " " + StringHelper.YELLOW + i));
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
