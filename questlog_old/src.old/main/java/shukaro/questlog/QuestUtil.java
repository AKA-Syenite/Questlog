package shukaro.questlog;

import cofh.lib.util.helpers.SecurityHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class QuestUtil
{
    public static MovingObjectPosition getObjectUnderMouse(EntityPlayer player)
    {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f + 1.62D - (double)player.yOffset;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 50.0D;
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        MovingObjectPosition hit = player.worldObj.rayTraceBlocks(vec3, vec31, true);

        Vec3 look = player.getLookVec();
        List entitiesPossiblyHitByVector = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(look.xCoord * d3, look.yCoord * d3, look.zCoord * d3).expand(1.0f, 1.0f, 1.0f));
        Iterator entityIterator = entitiesPossiblyHitByVector.iterator();
        while (entityIterator.hasNext())
        {
            Entity testEntity = (Entity)entityIterator.next();
            if (testEntity.canBeCollidedWith())
            {
                float bbExpansionSize = testEntity.getCollisionBorderSize();
                AxisAlignedBB entityBB = testEntity.boundingBox.expand(bbExpansionSize, bbExpansionSize, bbExpansionSize);
                if (entityBB.isVecInside(vec3))
                {
                    if (hit == null)
                        hit = new MovingObjectPosition(testEntity);
                    hit.entityHit = testEntity;
                    hit.typeOfHit = MovingObjectPosition.MovingObjectType.ENTITY;
                    return hit;
                }
            }
        }

        if (hit != null)
        {
            hit.typeOfHit = MovingObjectPosition.MovingObjectType.BLOCK;
            return hit;
        }

        return null;
    }

    public static EntityPlayer getPlayerByUUID(UUID uuid)
    {
        for (World world : MinecraftServer.getServer().worldServers)
        {
            for (EntityPlayer player : (List<EntityPlayer>)world.playerEntities)
            {
                if (SecurityHelper.getID(player).equals(uuid))
                    return player;
            }
        }
        return null;
    }

    public static String stackToString(ItemStack stack)
    {
        String out = Item.itemRegistry.getNameForObject(stack.getItem()) +
                "@" + (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ? "*" : stack.getItemDamage()) +
                "#" + (stack.hasTagCompound() ? stack.getTagCompound().toString() : "null");
        return out;
    }

    public static ItemStack stackFromString(String stackString)
    {
        String name = stackString.substring(0, stackString.indexOf('@'));
        String meta = stackString.substring(stackString.indexOf('@') + 1, stackString.indexOf('#'));
        String nbt = stackString.substring(stackString.indexOf('#') + 1, stackString.length());

        int intMeta = 0;
        if (meta.equals("*"))
            intMeta = OreDictionary.WILDCARD_VALUE;
        else
        {
            try
            {
                intMeta = Integer.parseInt(meta);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }

        NBTTagCompound tag = Questlog.gson.fromJson(nbt, NBTTagCompound.class);

        ItemStack stack = null;
        if (Item.itemRegistry.containsKey(name))
        {
            stack = new ItemStack((Item)Item.itemRegistry.getObject(name), 1, intMeta);
            stack.stackTagCompound = tag;
        }
        else if (Block.blockRegistry.containsKey(name))
        {
            stack = new ItemStack((Block)Block.blockRegistry.getObject(name), 1, intMeta);
            stack.stackTagCompound = tag;
        }
        return stack;
    }
}
