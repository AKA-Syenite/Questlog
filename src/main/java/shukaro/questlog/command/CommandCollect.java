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

import java.util.List;
import java.util.UUID;

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
    public void handleCommand(ICommandSender sender, String[] args)
    {
        if (args.length > 2)
        {
            if (args[1].equals("list"))
            {
                if (args.length == 3)
                {
                    String playerName = args[2];
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
                    int i = 0;
                    StringBuilder out = new StringBuilder();
                    for (String s : PlayerData.getCollectibles(targetUUID))
                    {
                        out.append(StringHelper.YELLOW + s + StringHelper.WHITE + ", ");
                        i++;
                    }
                    if (i > 0)
                        out.delete(out.length()-2, out.length());
                    else
                    {
                        sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nocollectibles")));
                        return;
                    }
                    sender.addChatMessage(new ChatComponentText(out.toString()));
                }
                else
                    throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            }
            else if (args[1].equals("has"))
            {
                if (args.length == 4)
                {
                    String collectible = args[2];
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
                    sender.addChatMessage(new ChatComponentText(PlayerData.getCollectibles(targetUUID).contains(collectible) ? StringHelper.localize("command.questlog.hascollectible") : StringHelper.localize("command.questlog.nothascollectible")));
                }
                else
                    throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            }
            else if (args[1].equals("add"))
            {
                if (args.length == 4)
                {
                    String collectible = args[2];
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
                    PlayerData.addCollectible(targetUUID, collectible);
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.addcollectible")));
                }
                else
                    throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            }
            else if (args[1].equals("remove"))
            {
                if (args.length == 4)
                {
                    String collectible = args[2];
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
                    PlayerData.removeCollectible(targetUUID, collectible);
                    sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.removecollectible")));
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
            return CommandBase.getListOfStringsMatchingLastWord(args, "list", "has", "add", "remove");
        if (args.length == 4 || (args.length == 3 && args[1].equals("list")))
            return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        return null;
    }
}
