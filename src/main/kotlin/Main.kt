import bsh.BSHPoller
import bsh.DeviceRegistry
import bsh.RoomRegistry
import bsh.ServiceRegistry
import bsh.client.Client
import bsh.client.LongPollingClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import netatmo.NetatmoPoller
import smarthome.actor.ActorRegistry
import smarthome.actor.ShutterActor
import smarthome.actor.TemperatureLevelActor
import smarthome.actor.ValveTappetActor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val log = KotlinLogging.logger {}
    log.info("Starting Smart Home Data Collector")

    val executor = Executors.newSingleThreadScheduledExecutor()
    executor.scheduleAtFixedRate(NetatmoPoller, 0, 10, TimeUnit.MINUTES)
    log.info("Netatmo Poller registered")
    RoomRegistry.rooms = Client.rooms()
    DeviceRegistry.devices = Client.devices()
    ServiceRegistry.services = Client.services()

    longpolling()
    setupActor()

    executor.scheduleAtFixedRate(BSHPoller, 0, 5, TimeUnit.MINUTES)
}

fun longpolling() {
    LongPollingClient.subscribe()
    LongPollingClient.startPolling()
}

fun setupActor() = GlobalScope.launch(Dispatchers.Default) {
buf    DeviceRegistry.devices
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