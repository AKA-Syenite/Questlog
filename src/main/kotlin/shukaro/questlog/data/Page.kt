package shukaro.questlog.data

data class Page(
        var nodes: Map<Node, Int>,
        override val unlocalizedName: String = "questlog.data.page"
): IContent