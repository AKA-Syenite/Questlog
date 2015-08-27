package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CommandEdit implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return null;
    }

    @Override
    public void handleCommand(ICommandSender iCommandSender, String[] strings)
    {

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender iCommandSender, String[] strings)
    {
        return null;
    }
}
