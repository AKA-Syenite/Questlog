package shukaro.questlog.data

data class Node(
        var unlocalizedName: String,
        var unlocalizedText: String,
        var prerequisites: Collection<Node>,
        var content: IContent,
        var addons: Collection<IAddon>
)