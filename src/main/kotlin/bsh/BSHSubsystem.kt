package bsh

import bsh.client.Client
import bsh.client.LongPollingClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import smarthome.ConfigHelper
import smarthome.actor.ActorRegistry
import smarthome.actor.ShutterActor
import smarthome.actor.TemperatureLevelActor
import smarthome.actor.ValveTappetActor
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object BSHSubsystem {
    val log = KotlinLogging.logger {}

    var config: BSH? = null

    fun start(executor: ScheduledExecutorService): Boolean {
        val configLoader = ConfigHelper.loadConfig<BSHConfig>()

        if (configLoader.isInvalid()) {
            log.warn("Invalid config: ${configLoader.mapInvalid { it.description() }}")
            return false
        }

        config = configLoader.getUnsafe().bsh
        log.info("Config loaded")

        RoomRegistry.rooms = Client.rooms()
        DeviceRegistry.devices = Client.devices()
        ServiceRegistry.services = Client.services()

        longpolling()
        setupActor()

        executor.scheduleAtFixedRate(BSHPoller, 0, 5, TimeUnit.MINUTES)

        log.info("Started.")
        return true
    }
}

private fun longpolling() {
    LongPollingClient.subscribe()
    LongPollingClient.startPolling()
}

private fun setupActor() = GlobalScope.launch(Dispatchers.Default) {
    DeviceRegistry.devices
        .forEach { d ->
            for (serviceId in d.deviceServiceIds) {
                when (serviceId) {
                    "ShutterContact" -> {
                        ActorRegistry.add(ShutterActor.instance(d))
                    }
                    "TemperatureLevel" -> {
                        ActorRegistry.add(TemperatureLevelActor.instance(d))
                    }
                    "ValveTappet" -> {
                        ActorRegistry.add(ValveTappetActor.instance(d))
                    }
                }
            }
        }
}

data class BSH(val ip: String, val port: String, val clientCertPassword: String, val influxBucket: String)
data class BSHConfig(
    val bsh: BSH
)