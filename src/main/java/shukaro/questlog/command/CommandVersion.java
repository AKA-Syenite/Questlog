package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import shukaro.questlog.Questlog;

import java.util.List;

public class CommandVersion implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return -1;
    }

    @Override
    public String getCommandName()
    {
        return "version";
    }

    @Override
    public void handleCommand(ICommandSender iCommandSender, String[] strings)
    {
        iCommandSender.addChatMessage(new ChatComponentText(StringHelper.YELLOW + Questlog.version));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender iCommandSender, String[] strings)
    {
        return null;
    }
}
