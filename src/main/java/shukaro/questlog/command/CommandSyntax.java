package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.StringHelper;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CommandSyntax implements ISubCommand
{
    public static TMap<String, String> syntaxTips = new THashMap<String, String>();

    static
    {
        syntaxTips.put("objectives", "command.questlog.objectives.syntax");
        syntaxTips.put("rewards", "command.questlog.rewards.syntax");
        syntaxTips.put("tags", "command.questlog.tags.syntax");
        syntaxTips.put("parents", "command.questlog.parents.syntax");
    }

    @Override
    public String getCommandName()
    {
        return "syntax";
    }

    @Override
    public int getPermissionLevel()
    {
        return -1;
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] arguments)
    {
        switch (arguments.length)
        {
            case 1:
                List<String> commandList = new ArrayList<String>(CommandHandler.getCommandList());
                Collections.sort(commandList, String.CASE_INSENSITIVE_ORDER);

                for (int i = 0; i < commandList.size() - 1; i++)
                {
                    String name = commandList.get(i);
                    if (CommandHandler.canUseCommand(sender, CommandHandler.getCommandPermission(name), name))
                    {
                        // FIXME: properly format this such that commands are clickable for auto-fill. paginate?
                        sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog." + name + ".syntax")));
                    }
                }
                break;
            case 2:
                String commandName = arguments[1];
                if (syntaxTips.containsKey(commandName))
                {
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize(syntaxTips.get(commandName))));
                    break;
                }
                if (!CommandHandler.getCommandExists(commandName))
                    throw new CommandNotFoundException("command.questlog.notFound");
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog." + commandName + ".syntax")));
                break;
            case 3:
                String command = arguments[1];
                String target = arguments[2];
                if (!CommandHandler.getCommandExists(command) || !CommandHandler.targets.contains(target))
                    throw new CommandNotFoundException("command.questlog.notFound");
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog." + command + "." + target + ".syntax")));
                break;
            default:
                throw new WrongUsageException("command.questlog." + getCommandName() + ".syntax");
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

        if (args.length == 2)
        {
            ArrayList<String> options = new ArrayList<String>();
            for (String s : CommandHandler.getCommandList())
                options.add(s);
            for (String s : syntaxTips.keySet())
                options.add(s);
            return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, options);
        }
        else if (args.length == 3)
            return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, CommandHandler.targets);
        return null;
    }

}