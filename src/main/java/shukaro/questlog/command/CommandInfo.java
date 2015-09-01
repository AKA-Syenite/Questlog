package shukaro.questlog.command;

import cofh.core.command.ISubCommand;
import cofh.lib.util.helpers.StringHelper;
import cofh.lib.util.position.BlockPosition;
import cofh.repack.codechicken.lib.raytracer.RayTracer;
import cofh.repack.codechicken.lib.vec.Cuboid6;
import cofh.repack.codechicken.lib.vec.Vector3;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import shukaro.questlog.QuestUtil;
import shukaro.questlog.Questlog;

import java.util.List;

public class CommandInfo implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 1;
    }

    @Override
    public String getCommandName()
    {
        return "info";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] args)
    {
        if (args.length > 1)
        {
            if (args[1].equals("held"))
            {
                if (args.length == 2)
                {
                    EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(sender.getCommandSenderName());
                    ItemStack held = null;
                    if (player != null)
                        held = player.getHeldItem();
                    if (held != null)
                    {
                        String stack = QuestUtil.stackToString(held);
                        sender.addChatMessage(new ChatComponentText(stack));
                        if (OreDictionary.getOreIDs(held).length > 0)
                        {
                            StringBuilder ores = new StringBuilder(StringHelper.LIGHT_GRAY + StringHelper.localize("command.questlog.oreentries") + StringHelper.WHITE + " ");
                            for (int id : OreDictionary.getOreIDs(held))
                                ores.append(OreDictionary.getOreName(id) + ", ");
                            ores.delete(ores.length()-2, ores.length());
                            sender.addChatMessage(new ChatComponentText(ores.toString()));
                        }
                    }
                    else
                        sender.addChatMessage(new ChatComponentText(StringHelper.YELLOW + StringHelper.localize("command.questlog.null") + StringHelper.END));
                }
                else
                    throw new WrongUsageException("command.questlog." + getCommandName() + "." + args[1] + ".syntax");
            }
            else if (args[1].equals("looking"))
            {
                if (args.length == 2)
                {
                    EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(sender.getCommandSenderName());
                    if (player != null)
                    {
                        MovingObjectPosition underMouse = QuestUtil.getObjectUnderMouse(player);
                        if (underMouse != null)
                        {
                            if (underMouse.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && underMouse.entityHit != null)
                            {
                                NBTTagCompound nbt = new NBTTagCompound();
                                if (!underMouse.entityHit.writeToNBTOptional(nbt))
                                    underMouse.entityHit.writeMountToNBT(nbt);
                                String entity = underMouse.entityHit.getClass().getName() + "#" + (nbt.hasNoTags() ? "null" : Questlog.gson.toJson(nbt));
                                sender.addChatMessage(new ChatComponentText(entity));
                                return;
                            }
                            else if (underMouse.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                            {
                                BlockPosition blockHit = new BlockPosition(underMouse.blockX, underMouse.blockY, underMouse.blockZ);
                                World world = player.worldObj;
                                TileEntity te = world.getTileEntity(blockHit.x, blockHit.y, blockHit.z);
                                NBTTagCompound nbt = new NBTTagCompound();
                                if (te != null)
                                    te.writeToNBT(nbt);
                                String block = Block.blockRegistry.getNameForObject(world.getBlock(blockHit.x, blockHit.y, blockHit.z)) +
                                        "@" + (world.getBlockMetadata(blockHit.x, blockHit.y, blockHit.z) == OreDictionary.WILDCARD_VALUE ? "*" : world.getBlockMetadata(blockHit.x, blockHit.y, blockHit.z)) +
                                        "#" + (nbt.hasNoTags() ? "null" : Questlog.gson.toJson(nbt));
                                sender.addChatMessage(new ChatComponentText(block));
                                return;
                            }
                        }
                    }
                    sender.addChatMessage(new ChatComponentText(StringHelper.YELLOW + StringHelper.localize("command.questlog.null") + StringHelper.END));
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
            return CommandBase.getListOfStringsMatchingLastWord(args, "held", "looking");
        return null;
    }
}
