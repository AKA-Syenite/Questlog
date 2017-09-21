package shukaro.questlog.data

import net.minecraft.nbt.NBTTagCompound

data class Page(
        override val TYPE: IContent.Type = IContent.Type.PAGE,
        var nodes: MutableMap<String, Int>,

        override val writeToNBT: () -> NBTTagCompound =  {
            var nbt: NBTTagCompound = NBTTagCompound()
            nbt.let { nodes.forEach { node -> it.setInteger(node.key, node.value) } }
            nbt
        },

        override val readFromNBT: (NBTTagCompound) -> Int = {
            it.keySet.forEach { key -> nodes.put(key, it.getInteger(key)) }
            nodes.size
        }
): IContent