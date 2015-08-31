package shukaro.questlog.data.questing;

import shukaro.questlog.data.PlayerData;
import shukaro.questlog.data.QuestData;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestManager
{
    public static HashMap<UUID, ArrayList<AbstractObjective>> runningObjectives = new HashMap<UUID, ArrayList<AbstractObjective>>();

    public static HashMap<Class, String> objectiveRegistry = new HashMap<Class, String>();
    public static HashMap<Class, String> rewardRegistry = new HashMap<Class, String>();

    public static boolean registerObjective(Class objectiveClass, String key, int numArgs)
    {
        if (objectiveRegistry.containsKey(objectiveClass))
            return false;
        objectiveRegistry.put(objectiveClass, key + "[" + numArgs + "]");
        return true;
    }

    public static boolean registerReward(Class rewardClass, String key, int numArgs)
    {
        if (rewardRegistry.containsKey(rewardClass))
            return false;
        rewardRegistry.put(rewardClass, key + "[" + numArgs + "]");
        return true;
    }

    public static void instantiateAllObjectivesForQuest(UUID playerUUID, String questUID)
    {
        if (PlayerData.getObjectives(playerUUID, questUID) == null)
            return;
        ArrayList<String> objectives = new ArrayList<String>(Arrays.asList(PlayerData.getObjectives(playerUUID, questUID)));
        if (!runningObjectives.keySet().contains(playerUUID))
            runningObjectives.put(playerUUID, new ArrayList<AbstractObjective>());
        for (String obj : objectives)
        {
            AbstractObjective ao = initObjectiveFromString(obj);
            if (ao != null)
            {
                ao.parentQuest = questUID;
                ao.parentUUID = playerUUID;
                if (!runningObjectives.get(playerUUID).contains(ao))
                {
                    runningObjectives.get(playerUUID).add(ao);
                    ao.start();
                }
            }
        }
    }

    protected static AbstractObjective initObjectiveFromString(String obj)
    {
        Pattern parser = Pattern.compile("(.+)\\((.*)\\)");
        Matcher result = parser.matcher(obj);
        if (result.matches() && result.groupCount() == 2)
        {
            String objectiveType = result.group(1).trim();
            String[] objectiveArgs = result.group(2).split("[,\\s]+");;
            if (objectiveArgs.length == getNumObjectiveArgs(objectiveType))
            {
                for (Map.Entry<Class, String> e : objectiveRegistry.entrySet())
                {
                    if (e.getValue().replaceAll("\\[[0-9]*\\]", "").equals(objectiveType))
                    {
                        try
                        {
                            return ((AbstractObjective)e.getKey().newInstance()).init(objectiveArgs);
                        }
                        catch (Exception x)
                        {
                            x.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static ArrayList<AbstractObjective> getRunningObjectivesForPlayer(UUID playerUUID, String questUID)
    {
        ArrayList<AbstractObjective> running = new ArrayList<AbstractObjective>();
        if (runningObjectives.keySet().contains(playerUUID))
        {
            for (AbstractObjective ao : runningObjectives.get(playerUUID))
            {
                if (ao.parentQuest.equals(questUID))
                    running.add(ao);
            }
        }
        return running;
    }

    protected static void removeObjectivesForPlayer(UUID player, String questUID)
    {
        if (!runningObjectives.keySet().contains(player))
            return;
        for (AbstractObjective ao : runningObjectives.get(player))
        {
            if (ao.parentQuest.equals(questUID))
                runningObjectives.get(player).remove(ao);
        }
    }

    public static boolean isCompleted(UUID player, String questUID)
    {
        if (!runningObjectives.keySet().contains(player))
            return false;
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
                    return false;
            }
        }
        return true;
    }

    public static void tryComplete(UUID player, String questUID)
    {
        if (isCompleted(player, questUID))
        {
            giveRewards(player, questUID);
            PlayerData.setQuestCompletion(player, questUID, true);
            removeObjectivesForPlayer(player, questUID);
        }
    }

    public static void giveRewards(UUID player, String questUID)
    {
        ArrayList<String> rewards = QuestData.getQuestRewards(questUID);
        for (String reward : rewards)
            doReward(player, reward);
    }

    protected static void doReward(UUID player, String reward)
    {
        Pattern parser = Pattern.compile("(.+)\\((.*)\\)");
        Matcher result = parser.matcher(reward);
        if (result.groupCount() == 2)
        {
            String rewardType = result.group(1).trim();
            String[] rewardArgs = result.group(2).split("[,\\s]+");;
            if (rewardArgs.length == getNumRewardArgs(rewardType))
            {
                for (Map.Entry<Class, String> e : rewardRegistry.entrySet())
                {
                    if (e.getValue().equals(rewardType))
                    {
                        try
                        {
                            ((AbstractReward)e.getKey().newInstance()).give(player, rewardArgs);
                            return;
                        }
                        catch (Exception x)
                        {
                            x.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    protected static int getNumObjectiveArgs(String objective)
    {
        Pattern parser = Pattern.compile("(.+)\\[([0-9]+)\\]");
        for (String key : objectiveRegistry.values())
        {
            Matcher result = parser.matcher(key);
            if (result.matches() && result.group(1).trim().equals(objective))
                return Integer.parseInt(result.group(2).trim());
        }
        return 0;
    }

    protected static int getNumRewardArgs(String reward)
    {
        Pattern parser = Pattern.compile("(.+)\\[([0-9]+)\\]");
        for (String key : rewardRegistry.values())
        {
            Matcher result = parser.matcher(key);
            if (result.matches() && result.group(1).trim().equals(reward))
                return Integer.parseInt(result.group(2).trim());
        }
        return 0;
    }
}
