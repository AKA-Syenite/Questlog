package shukaro.questlog.data.questing;

public abstract class AbstractObjective
{
    public String parentQuest;
    public boolean isFulfilled;

    public abstract AbstractObjective start(String[] args);
}
