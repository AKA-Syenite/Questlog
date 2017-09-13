package shukaro.questlog

import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.*
import org.apache.logging.log4j.Logger
import shukaro.questlog.data.Node
import shukaro.questlog.sided.CommonProxy

@Mod(modid = Questlog.MODID, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter", useMetadata = true)
object Questlog {

    const val MODID = "questlog"

    lateinit var config: Configuration
    lateinit var logger: Logger

    @SidedProxy(clientSide = "shukaro.questlog.sided.ClientProxy", serverSide = "shukaro.questlog.sided.ServerProxy")
    lateinit var proxy: CommonProxy

    var questNodes: HashSet<Node> = HashSet()

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) = proxy.serverStarting(event)

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) = proxy.preInit(event)

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) = proxy.init(event)

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) = proxy.postInit(event)

    @Mod.EventHandler
    fun imcRequest(event: FMLInterModComms) = proxy.imcRequest(event)

}