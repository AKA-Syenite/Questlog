package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import shukaro.questlog.data.QuestData;

import java.util.List;

public class CommandNewQuest implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandName()
    {
        return "newQuest";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 5:
                String questUID = args[1];
                String[] objectives = args[2].split(";");
                String[] rewards = args[3].split(";");
                String[] tags = args[4].split(";");
                if (QuestData.getQuest(questUID) != null)
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.uidtaken")));
                    break;
                }
                else
                {
                    QuestData.createQuest(questUID, objectives, rewards, tags);
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.questcreated")));
                    break;
                }
            default:
                throw new WrongUsageException("command.questlog." + getCommandName() + ".syntax");
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }
}
