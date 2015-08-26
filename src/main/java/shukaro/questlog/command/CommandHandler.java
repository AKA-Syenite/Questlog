package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.StringHelper;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Courtesy of CoFHCore's command implementation
 */
public class CommandHandler extends CommandBase
{
    public static final String COMMAND_DISALLOWED = StringHelper.LIGHT_RED + StringHelper.localize("command.questlog.disallowed");

    private static TMap<String, ISubCommand> commands = new THashMap<String, ISubCommand>();

    static
    {
        registerSubCommand(new CommandVersion());
        registerSubCommand(new CommandHelp());
        registerSubCommand(new CommandSyntax());
    }

    public static boolean registerSubCommand(ISubCommand subCommand)
    {
        if (!commands.containsKey(subCommand.getCommandName()))
        {
            commands.put(subCommand.getCommandName(), subCommand);
            return true;
        }
        return false;
    }

    public static Set<String> getCommandList()
    {
        return commands.keySet();
    }

    public static int getCommandPermission(String command)
    {
        return getCommandExists(command) ? commands.get(command).getPermissionLevel() : Integer.MAX_VALUE;
    }

    public static boolean getCommandExists(String command)
    {
        return commands.containsKey(command);
    }

    public static boolean canUseCommand(ICommandSender sender, int permission, String name)
    {
        if (getCommandExists(name))
        {
            return sender.canCommandSenderUseCommand(permission, "questlog " + name) ||
                    // this check below is because mojang is incompetent, as always
                    (sender instanceof EntityPlayerMP && permission <= 0);
        }
        return false;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return -1;
    }

    private static DummyCommand dummy = new DummyCommand();

    public static void logAdminCommand(ICommandSender sender, ISubCommand command, String info, Object... data)
    {
        dummy.setFromCommand(command);
        for (int i = 0, e = data.length; i < e; ++i) {
            Object entry = data[i];
            if (entry instanceof Number) {
                Number d = (Number) entry;
                int a = d.intValue();
                float f = d.floatValue();
                if (a != f) {
                    data[i] = String.format("%.2f", f);
                }
            }
        }
        CommandBase.func_152373_a(sender, dummy, info, data);
    }

    @Override
    public String getCommandName()
    {
        return "questlog";
    }

    @Override
    public List getCommandAliases() {

        return Collections.singletonList("ql");
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/" + getCommandName() + " help";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] arguments)
    {
        if (arguments.length < 1)
            arguments = new String[] { "help" };
        ISubCommand command = commands.get(arguments[0]);
        if (command != null)
        {
            if (canUseCommand(sender, command.getPermissionLevel(), command.getCommandName()))
            {
                command.handleCommand(sender, arguments);
                return;
            }
            throw new CommandException("commands.generic.permission");
        }
        throw new CommandNotFoundException("command.questlog.notFound");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1)
            return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, commands.keySet());
        else if (commands.containsKey(par2ArrayOfStr[0]))
            return commands.get(par2ArrayOfStr[0]).addTabCompletionOptions(par1ICommandSender, par2ArrayOfStr);
        return null;
    }

    private static class DummyCommand extends CommandBase
    {
        private int perm = 4;
        private String name = "";

        public void setFromCommand(ISubCommand command)
        {
            name = command.getCommandName();
            perm = command.getPermissionLevel();
        }

        @Override
        public String getCommandName()
        {
            return "questlog " + name;
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return perm;
        }

        @Override
        public String getCommandUsage(ICommandSender p_71518_1_)
        {
            return "";
        }

        @Override
        public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_)
        {

        }

    }
}
