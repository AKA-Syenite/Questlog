package shukaro.questlog.data.questing.objectives;

import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import scala.collection.parallel.ParIterableLike;
import shukaro.questlog.data.PlayerData;
import shukaro.questlog.data.QuestData;
import shukaro.questlog.data.questing.AbstractObjective;
import shukaro.questlog.data.questing.QuestManager;

public class ObjectiveKillEntity extends AbstractObjective
{
    private Class entityClass;

    private int killed;
    private int toKill;

    @Override
    public AbstractObjective init(String[] args)
    {
        try
        {
            entityClass = Class.forName(args[0]);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        try
        {
            killed = Integer.parseInt(args[1]);
            toKill = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void start()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void stop()
    {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public String saveToString()
    {
        return "killEntity(" + entityClass.getName() + "," + killed + "," + toKill + ")";
    }

    @Override
    public String getLocalizedText()
    {
        return StringHelper.localize("objective.questlog.kill") + " " + StringHelper.localize((String)EntityList.classToStringMapping.get(entityClass)) + "s " + killed + "/" + toKill;
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent e)
    {
        if (!(e.source.getEntity() instanceof EntityPlayer) || !e.entityLiving.getClass().equals(entityClass))
            return;
        if (SecurityHelper.getID((EntityPlayer)e.source.getEntity()).equals(parentUUID))
        {
            if (killed < toKill)
            {
                killed++;
                PlayerData.updateObjectives(parentUUID, parentQuest);
            }
            if (killed >= toKill && QuestData.hasTag(parentQuest, "autocomplete"))
                QuestManager.tryComplete(parentUUID, parentQuest);
        }
    }
}
