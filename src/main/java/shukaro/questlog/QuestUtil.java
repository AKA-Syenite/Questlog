package shukaro.questlog;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

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
}
