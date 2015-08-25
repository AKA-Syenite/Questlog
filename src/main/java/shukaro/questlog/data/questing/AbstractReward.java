package shukaro.questlog.data.questing;

import java.util.UUID;

public abstract class AbstractReward
{
    public abstract void give(UUID player, String[] args);
}
