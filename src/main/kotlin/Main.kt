import bsh.DeviceRegistry
import bsh.EnrichedDevice
import bsh.RoomRegistry
import bsh.ServiceRegistry
import bsh.client.Client
import bsh.client.FuelConfig.configFuel
import bsh.client.LongPollingClient
import com.influxdb.client.domain.WritePrecision
import smarthome.ConfigHelper
import smarthome.convertBoschSmartHomeToInflux

fun main(args: Array<String>) {
    configFuel()
    longpolling()
    while (true) {
        val now = System.currentTimeMillis()

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

        //InfluxClient.push(points)
        println("${points.size} points sent")
        Thread.sleep(1000 * 60 * ConfigHelper.config.smarthome.interval)
    }
}

fun longpolling() {
    LongPollingClient.subscribe()
    LongPollingClient.startPolling()
}