package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CommandCollect implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandName()
    {
        return "collect";
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
