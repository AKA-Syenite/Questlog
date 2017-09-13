package shukaro.questlog.data.questing;

import java.util.UUID;

public abstract class AbstractReward
{
    /**
     * Give whatever the reward is
     * @param player the target player
     * @param args any parameters needed by the reward
     */
    public abstract void give(UUID player, String[] args);

    /**
     * Gets the full localized text of the reward (IE "64 Blocks of Diamond" or something)
     * @return a localized string
     */
    public abstract String getLocalizedText();
}
