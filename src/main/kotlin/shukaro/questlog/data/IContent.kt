package shukaro.questlog.data

import net.minecraft.nbt.NBTBase

interface IContent {
    val TYPE: Type

    val writeToNBT: Function<NBTBase>
    val readFromNBT: Function<Int>

    enum class Type { PAGE, QUEST }
}