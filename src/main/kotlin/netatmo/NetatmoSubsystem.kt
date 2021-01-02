package netatmo

import bsh.BSHSubsystem
import mu.KotlinLogging
import smarthome.ConfigHelper
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object NetatmoSubsystem {
    val log = KotlinLogging.logger {}

    var config: Netatmo? = null

    fun start(executor: ScheduledExecutorService): Boolean {
        val configLoader = ConfigHelper.loadConfig<Netatmo>()
        if (configLoader.isInvalid()) {
            BSHSubsystem.log.warn("Invalid config: ${configLoader.mapInvalid { it.description() }}")
            return false
        }
        config = configLoader.getUnsafe()
        log.info("Config loaded")

        executor.scheduleAtFixedRate(NetatmoPoller, 0, 10, TimeUnit.MINUTES)
        log.info("Netatmo Poller registered")
        log.info("Started")
        return true
    }
}

data class Netatmo(
    val user: String,
    val password: String,
    val clientID: String,
    val clientSecret: String,
    val influxBucket: String
)

data class NetatmoConfig(
    val netatmo: Netatmo
)