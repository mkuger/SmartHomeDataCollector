import bsh.DeviceRegistry
import bsh.EnrichedDevice
import bsh.RoomRegistry
import bsh.ServiceRegistry
import bsh.client.Client
import bsh.client.LongPollingClient
import com.influxdb.client.domain.WritePrecision
import influxdb.InfluxClient
import kotlinx.coroutines.runBlocking
import smarthome.ConfigHelper
import smarthome.actor.ActorRegistry
import smarthome.actor.shutterActor
import smarthome.convertBoschSmartHomeToInflux
import java.time.Instant

fun main(args: Array<String>) {
    longpolling()
    while (true) {
        val now = Instant.now()

        RoomRegistry.rooms = Client.rooms()
        DeviceRegistry.devices = Client.devices()
        ServiceRegistry.services = Client.services()

        val points = DeviceRegistry.devices
            .map { d ->
                val services = ServiceRegistry.servicesByDevice(d)
                EnrichedDevice(d, services)
            }
            .mapNotNull { d -> convertBoschSmartHomeToInflux(d) }
            .flatten()
            .map { p -> p.time(now, WritePrecision.S) }

        InfluxClient.push(points)
        println("${points.size} points sent")
        setupActor()
        Thread.sleep(1000 * 60 * ConfigHelper.config.smarthome.interval)
    }
}

fun longpolling() {
    LongPollingClient.subscribe()
    LongPollingClient.startPolling()
}

fun setupActor() = runBlocking {
    DeviceRegistry.devices
        .filter { d -> d.deviceServiceIds.contains("ShutterContact") }
        .forEach { d ->
            ActorRegistry.add(shutterActor(d))
        }
}