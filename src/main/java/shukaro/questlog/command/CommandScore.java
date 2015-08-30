package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import shukaro.questlog.data.PlayerData;
import shukaro.questlog.data.QuestData;

import java.util.List;
import java.util.UUID;

public class CommandScore implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandName()
    {
        return "score";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] args)
    {
        if (args.length > 2)
        {
            if (args[1].equals("mod"))
            {
                if (args.length == 5)
                {
                    String scoreUID = args[2];
                    String playerName = args[3];
                    String amount = args[4];
                    int intAmount;
                    try
                    {
                        intAmount = Integer.parseInt(amount);
                    }
                    catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                        sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.notanumber")));
                        return;
                    }
                    EntityPlayer player = null;
                    for (World world : MinecraftServer.getServer().worldServers)
                    {
                        for (EntityPlayer p : (List<EntityPlayer>)world.playerEntities)
                        {
                            if (p.getDisplayName().equals(playerName))
                            {
                                player = p;
                                break;
                            }
                        }
                    }
                    if (player == null)
                    {
                        sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchplayer")));
                        return;
                    }
                    UUID targetUUID = player.getPersistentID();
                    PlayerData.modScore(targetUUID, scoreUID, intAmount);
                }
                else
                    throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            }
            else if (args[1].equals("delete"))
            {
                if (args.length == 4)
                {
                    String scoreUID = args[2];
                    String playerName = args[3];
                    EntityPlayer player = null;
                    for (World world : MinecraftServer.getServer().worldServers)
                    {
                        for (EntityPlayer p : (List<EntityPlayer>)world.playerEntities)
                        {
                            if (p.getDisplayName().equals(playerName))
                            {
                                player = p;
                                break;
                            }
                        }
                    }
                    if (player == null)
                    {
                        sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchplayer")));
                        return;
                    }
                    UUID targetUUID = player.getPersistentID();
                    PlayerData.deleteScore(targetUUID, scoreUID);
                }
                else
                    throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            }
            else
                throw new WrongUsageException("command.questlog." + getCommandName() + ".syntax");
        }
        else
            throw new WrongUsageException("command.questlog." + getCommandName() + ".syntax");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, "mod", "delete");
        return null;
    }
}
