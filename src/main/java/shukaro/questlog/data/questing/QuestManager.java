package shukaro.questlog.data.questing;

import net.minecraft.entity.player.EntityPlayer;
import shukaro.questlog.data.QuestData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestManager
{
    public static HashMap<UUID, ArrayList<AbstractObjective>> runningObjectives = new HashMap<UUID, ArrayList<AbstractObjective>>();
    public static HashMap<AbstractObjective, String> objectiveRegistry = new HashMap<AbstractObjective, String>();
    public static HashMap<AbstractReward, String> rewardRegistry = new HashMap<AbstractReward, String>();

    public static void instantiateObjectives(EntityPlayer player, String questUID)
    {
        instantiateObjectives(player.getPersistentID(), questUID);
    }

    public static void instantiateObjectives(UUID player, String questUID)
    {
        ArrayList<String> objectives = QuestData.getQuestObjectives(questUID);
        if (!runningObjectives.keySet().contains(player))
            runningObjectives.put(player, new ArrayList<AbstractObjective>());
        for (String obj : objectives)
            runningObjectives.get(player).add(startObjective(obj));
    }

    public static AbstractObjective startObjective(String obj)
    {
        Pattern parser = Pattern.compile("(.+)\\((.*)\\)");
        Matcher result = parser.matcher(obj);
        if (result.groupCount() == 2)
        {
            String objectiveType = result.group(0).trim();
            String[] objectiveArgs = result.group(1).split("[,\\s]+");;
            if (objectiveArgs.length == getNumObjectiveArgs(objectiveType))
            {
                for (Map.Entry<AbstractObjective, String> e : objectiveRegistry.entrySet())
                {
                    if (e.getValue().equals(objectiveType))
                        return e.getKey().start(objectiveArgs);
                }
            }
        }
        return null;
    }

    public static void removeObjectives(EntityPlayer player, String questUID)
    {
        removeObjectives(player.getPersistentID(), questUID);
    }

    public static void removeObjectives(UUID player, String questUID)
    {
        if (!runningObjectives.keySet().contains(player))
            return;
        for (AbstractObjective ao : runningObjectives.get(player))
        {
            if (ao.parentQuest.equals(questUID))
                runningObjectives.get(player).remove(ao);
        }
    }

    public static void checkCompletion(EntityPlayer player, String questUID)
    {
        checkCompletion(player.getPersistentID(), questUID);
    }

    public static void checkCompletion(UUID player, String questUID)
    {
        if (!runningObjectives.keySet().contains(player))
            return;
        ArrayList<AbstractObjective> objectives = new ArrayList<AbstractObjective>();
        for (AbstractObjective ao : runningObjectives.get(player))
        {
            if (ao.parentQuest.equals(questUID))
                objectives.add(ao);
        }
        if (!objectives.isEmpty())
        {
            for (AbstractObjective ao : objectives)
            {
                if (!ao.isFulfilled)
                    return;
            }
            giveRewards(player, questUID);
        }
    }

    public static void giveRewards(EntityPlayer player, String questUID)
    {
        giveRewards(player.getPersistentID(), questUID);
    }

    public static void giveRewards(UUID player, String questUID)
    {
        ArrayList<String> rewards = QuestData.getQuestRewards(questUID);
        for (String reward : rewards)
            doReward(player, reward);
    }

    public static void doReward(UUID player, String reward)
    {
        Pattern parser = Pattern.compile("(.+)\\((.*)\\)");
        Matcher result = parser.matcher(reward);
        if (result.groupCount() == 2)
        {
            String rewardType = result.group(0).trim();
            String[] rewardArgs = result.group(1).split("[,\\s]+");;
            if (rewardArgs.length == getNumRewardArgs(rewardType))
            {
                for (Map.Entry<AbstractReward, String> e : rewardRegistry.entrySet())
                {
                    if (e.getValue().equals(rewardType))
                    {
                        e.getKey().give(player, rewardArgs);
                        return;
                    }
                }
            }
        }
    }

    public static int getNumObjectiveArgs(String objective)
    {
        Pattern parser = Pattern.compile("(.+)\\[([0-9]+)\\]");
        for (String key : objectiveRegistry.values())
        {
            Matcher result = parser.matcher(key);
            if (result.group(0).trim().equals(objective))
                return Integer.parseInt(result.group(1).trim());
        }
        return 0;
    }

    public static int getNumRewardArgs(String reward)
    {
        Pattern parser = Pattern.compile("(.+)\\[([0-9]+)\\]");
        for (String key : rewardRegistry.values())
        {
            Matcher result = parser.matcher(key);
            if (result.group(0).trim().equals(reward))
                return Integer.parseInt(result.group(1).trim());
        }
        return 0;
    }
}
