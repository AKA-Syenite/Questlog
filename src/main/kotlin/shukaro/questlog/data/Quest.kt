package shukaro.questlog.data

import net.minecraft.nbt.NBTTagCompound

data class Quest(
        override val TYPE: IContent.Type = IContent.Type.QUEST,

        var objectives: List<IObjective>,
        var rewards: List<IReward>,

        override val writeToNBT: () -> NBTTagCompound = {
            var nbt: NBTTagCompound = NBTTagCompound()
            nbt.let { nodes.forEach { node -> it.setInteger(node.key, node.value) } }
            nbt
        },

        override val readFromNBT: () -> Page = {

        }
): IContent