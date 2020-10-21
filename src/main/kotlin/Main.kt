import bsh.Client
import bsh.EnrichedDevice
import bsh.FuelConfig.configFuel
import influxdb.InfluxClient
import smarthome.ConfigHelper
import smarthome.convertBoschSmartHomeToInflux

fun main(args: Array<String>) {
    while (true) {
        configFuel()
        val points = Client.devices()
            .map { d ->
                val services = Client.servicesByDevice(d.id)
                EnrichedDevice(d, services)
            }
            .mapNotNull { d -> convertBoschSmartHomeToInflux(d) }
            .flatten()

        InfluxClient.push(points)
        println("${points.size} points sent")
        Thread.sleep(1000 * 60 * ConfigHelper.config.smarthome.interval)
    }
}