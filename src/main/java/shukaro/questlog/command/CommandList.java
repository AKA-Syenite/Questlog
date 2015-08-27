package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CommandList implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 1;
    }

    @Override
    public String getCommandName()
    {
        return "list";
    }

    @Override
    public void handleCommand(ICommandSender iCommandSender, String[] strings)
    {

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
