package shukaro.questlog.data.questing;

import java.util.UUID;

public abstract class AbstractObjective
{
    /**
     * The quest UID that this specific instance is attached to.
     */
    public String parentQuest = "";

    /**
     * The player UUID that this specific instance is attached to.
     */
    public UUID parentUUID = null;

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
    public abstract AbstractObjective init(String[] args);

    /**
     * Begin tracking the objective
     */
    public abstract void start();

    /**
     * Write out the current state of the objective to a string
     * @return Args that can be passed into start(String[]) to resume quest tracking
     */
    public abstract String saveToString();

    /**
     * Gets the full localized text of the objective (IE "Craft Torches 0/16", "Submit Bear Asses 12/100")
     * @return a localized string
     */
    public abstract String getLocalizedText();

    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof AbstractObjective))
            return false;
        return saveToString().equals(((AbstractObjective)o).saveToString());
    }
}
