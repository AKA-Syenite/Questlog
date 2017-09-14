package shukaro.questlog.sided

import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.event.*
import shukaro.questlog.Config
import shukaro.questlog.Questlog

open class CommonProxy {

    fun serverStarting(event: FMLServerStartingEvent){}
    fun postInit(event: FMLPostInitializationEvent){}
    fun imcRequest(event: FMLInterModComms){}

    fun preInit(event: FMLPreInitializationEvent) {
        Questlog.logger = event.modLog
        Questlog.config = Configuration(event.suggestedConfigurationFile)
        Config.readConfig()
    }

    fun init(event: FMLInitializationEvent) {

    }
}