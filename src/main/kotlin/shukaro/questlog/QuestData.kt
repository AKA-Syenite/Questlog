package shukaro.questlog

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import shukaro.questlog.data.Node

object QuestData: WorldSavedData(Questlog.MODID + "_QuestData") {

    val DATA_NAME = Questlog.MODID + "_QuestData"

    var questNodes: HashSet<Node> = HashSet()

    fun get(world: World): QuestData? {
        var instance = world.mapStorage?.getOrLoadData(QuestData::class.java, DATA_NAME) as QuestData?
        if (instance == null) {
            instance = QuestData
            world.mapStorage?.setData(DATA_NAME, instance)
        }
        return instance
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        var questlogData: NBTTagList = NBTTagList()
        for (node in questNodes) {
            var nodeData: NBTTagCompound = NBTTagCompound()
            nodeData.setString("unlocalizedName", node.unlocalizedName)
            nodeData.setString("unlocalizedText", node.unlocalizedText)
            var prerequisites: NBTTagList = NBTTagList()
            for (prereqNode in node.prerequisites) {
                prerequisites.appendTag(NBTTagString(prereqNode.unlocalizedName))
            }
            nodeData.setTag("prerequisites", prerequisites)
            nodeData.setString("content", node.content.unlocalizedName)
            var addons: NBTTagList = NBTTagList()
            for (addon in node.addons) {
                addons.appendTag(NBTTagString(addon.unlocalizedName))
            }
            nodeData.setTag("addons", addons)
            questlogData.appendTag(nodeData)
        }
        nbt.setTag("questlogData", questlogData)
        return nbt
    }

    override fun readFromNBT(nbt: NBTTagCompound?) {

    }
}