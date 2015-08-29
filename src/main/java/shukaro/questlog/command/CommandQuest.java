package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import shukaro.questlog.data.PlayerData;
import shukaro.questlog.data.QuestData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CommandQuest implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandName()
    {
        return "quest";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 4)
        {
            String op = args[1];
            String playerName = args[2];
            String questUID = args[3];
            if (QuestData.getQuest(questUID) == null)
            {
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid")));
                return;
            }
            if (!Arrays.asList(MinecraftServer.getServer().getAllUsernames()).contains(playerName))
            {
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchplayer")));
                return;
            }
            UUID playerUUID = null;
            for (World world : MinecraftServer.getServer().worldServers)
            {
                for (EntityPlayer player : (List<EntityPlayer>)world.playerEntities)
                {
                    if (player.getDisplayName().equals(playerName))
                    {
                        playerUUID = player.getPersistentID();
                        break;
                    }
                }
            }
            if (playerUUID != null)
            {
                if (op.equals("add"))
                {
                    PlayerData.giveQuest(playerUUID, questUID);
                    if (ServerHelper.isMultiPlayerServer())
                    {
                        //sync
                    }
                }
                else if (op.equals("remove"))
                {
                    PlayerData.removeQuest(playerUUID, questUID);
                    if (ServerHelper.isMultiPlayerServer())
                    {
                        //sync
                    }
                }
                else
                    throw new WrongUsageException("command.questlog." + getCommandName() + ".syntax");
            }
        }
        else
            throw new WrongUsageException("command.questlog." + getCommandName() + ".syntax");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, new String[]{"add", "remove"});
        else if (args.length == 3)
            return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        return null;
    }
}
