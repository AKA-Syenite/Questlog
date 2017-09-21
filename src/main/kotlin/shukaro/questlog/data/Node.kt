package shukaro.questlog.data

data class Node(
        var unlocalizedName: String,
        var unlocalizedText: String,
        var prerequisites: List<String>,
        var content: IContent,
        var addons: List<IAddon>
)