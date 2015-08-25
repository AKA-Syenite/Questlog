package shukaro.questlog.data.questing;

public abstract class AbstractObjective
{
    /**
     * The quest UID that this specific instance is attached to.
     * Do not modify
     */
    public String parentQuest = "";

    /**
     * Whether or not the objective is completed or otherwise fulfilled
     */
    public boolean isFulfilled = false;

    /**
     * Instantiate a new instance of this objective with the given args and return it.
     * Register the instance to whatever listeners or handlers necessary
     * @param args Parameters needed by the objective
     * @return a new AbstractObjective instance
     */
    public abstract AbstractObjective start(String[] args);
}
