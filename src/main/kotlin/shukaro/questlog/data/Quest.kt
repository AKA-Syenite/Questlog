package shukaro.questlog.data

data class Quest(
        var objectives: Collection<IObjective>,
        var rewards: Collection<IReward>
): IContent