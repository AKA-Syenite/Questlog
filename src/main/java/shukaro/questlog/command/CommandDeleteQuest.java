package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import shukaro.questlog.data.QuestData;

import java.util.List;

public class CommandDeleteQuest implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 3;
    }

    @Override
    public String getCommandName()
    {
        return "deleteQuest";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 2:
                String questUID = args[1];
                if (questUID.equals("\"\"") || questUID.equals("''"))
                    questUID = "";
                if (QuestData.removeQuest(questUID))
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.questdeleted")));
                    if (ServerHelper.isMultiPlayerServer())
                    {
                        //sync
                    }
                }
                else
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchquest")));
                break;
            default:
                throw new WrongUsageException("command.questlog." + getCommandName() + ".syntax");
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender iCommandSender, String[] strings)
    {
        return null;
    }
}
