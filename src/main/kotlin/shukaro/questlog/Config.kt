package shukaro.questlog

import net.minecraft.client.resources.I18n
import net.minecraftforge.common.config.Configuration

object Config {

    //Category names for consistency
    private val CATEGORY_GENERAL = "general"
    private val CATEGORY_CLIENT = "client"

    //Default values
    var trackerX = 10
    var trackerY = 10

    fun readConfig() {
        val cfg = Questlog.config
        try {
            cfg.load()
            initConfig(cfg)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cfg.hasChanged()) {
                cfg.save()
            }
        }
    }

    private fun initConfig(cfg: Configuration) {
        trackerX = cfg.getInt("trackerX", CATEGORY_CLIENT, trackerX, 0, 10000, I18n.format("en_US", "questlog.config.trackerx"))
        trackerY = cfg.getInt("trackerY", CATEGORY_CLIENT, trackerY, 0, 10000, I18n.format("en_US", "questlog.config.trackery"))
    }

}