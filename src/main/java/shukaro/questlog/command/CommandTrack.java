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
import shukaro.questlog.data.PlayerData;
import shukaro.questlog.data.QuestData;

import java.util.List;
import java.util.UUID;

public class CommandTrack implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "track";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            UUID playerUUID = null;
            for (EntityPlayer player : (List<EntityPlayer>)sender.getEntityWorld().playerEntities)
            {
                if (player.getCommandSenderName().equals(sender.getCommandSenderName()))
                {
                    playerUUID = player.getPersistentID();
                    break;
                }
            }
            if (playerUUID == null)
                throw new WrongUsageException("command.questlog.onlyplayers");
            StringBuilder out = new StringBuilder(StringHelper.localize("command.questlog.tracking") + " ");
            int numTracked = PlayerData.getTrackedQuests(playerUUID).size();
            for (String questUID : PlayerData.getTrackedQuests(playerUUID))
                out.append(StringHelper.YELLOW + StringHelper.localize("quest.questlog." + questUID + ".name") + StringHelper.END + ", ");
            if (numTracked > 0)
                out.delete(out.length()-2, out.length());
            sender.addChatMessage(new ChatComponentText(out.toString()));
            sender.addChatMessage(new ChatComponentText(StringHelper.WHITE + StringHelper.localize("command.questlog.total") + " " + StringHelper.YELLOW + numTracked));
        }
        else if (args.length == 2)
        {
            UUID playerUUID = null;
            for (EntityPlayer player : (List<EntityPlayer>)sender.getEntityWorld().playerEntities)
            {
                if (player.getCommandSenderName().equals(sender.getCommandSenderName()))
                {
                    playerUUID = player.getPersistentID();
                    break;
                }
            }
            if (playerUUID == null)
                throw new WrongUsageException("command.questlog.onlyplayers");

            String questUID = args[1];
            if (QuestData.getQuest(questUID) == null)
            {
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid")));
                return;
            }
            else if (!PlayerData.playerHasQuest(playerUUID, questUID) || (PlayerData.playerHasQuest(playerUUID, questUID) && PlayerData.getQuestCompletion(playerUUID, questUID) && !PlayerData.isTrackingQuest(playerUUID, questUID)))
            {
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.canttrack")));
                return;
            }
            PlayerData.setTrackingQuest(playerUUID, questUID, !PlayerData.isTrackingQuest(playerUUID, questUID));
        }
        else if (args.length == 3)
        {
            EntityPlayer senderEntity = null;
            for (EntityPlayer p : (List<EntityPlayer>)sender.getEntityWorld().playerEntities)
            {
                if (p.getCommandSenderName().equals(sender.getCommandSenderName()))
                {
                    senderEntity = p;
                    break;
                }
            }
            if (senderEntity != null && !MinecraftServer.getServer().getConfigurationManager().func_152596_g(senderEntity.getGameProfile()))
                throw new WrongUsageException("command.questlog.disallowed");
            String questUID = args[1];
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
            if (QuestData.getQuest(questUID) == null)
            {
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.nosuchuid")));
                return;
            }
            else if (!PlayerData.playerHasQuest(targetUUID, questUID) || (PlayerData.playerHasQuest(targetUUID, questUID) && PlayerData.getQuestCompletion(targetUUID, questUID) && !PlayerData.isTrackingQuest(targetUUID, questUID)))
            {
                sender.addChatMessage(new ChatComponentText(StringHelper.localize("command.questlog.canttrack")));
                return;
            }
            PlayerData.setTrackingQuest(targetUUID, questUID, !PlayerData.isTrackingQuest(targetUUID, questUID));
            if (ServerHelper.isMultiPlayerServer())
            {
                //sync
            }
        }
        else
            throw new WrongUsageException("command.questlog." + getCommandName() + ".syntax");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, QuestData.getQuestIDs().toArray(new String[QuestData.getQuestIDs().size()]));
        else if (args.length == 3)
            return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        return null;
    }
}
