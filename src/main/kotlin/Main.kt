import bsh.Client
import bsh.EnrichedDevice
import bsh.FuelConfig.configFuel
import com.influxdb.client.domain.WritePrecision
import influxdb.InfluxClient
import smarthome.ConfigHelper
import smarthome.convertBoschSmartHomeToInflux

fun main(args: Array<String>) {
    while (true) {
        configFuel()
        val now = System.currentTimeMillis()
        val points = Client.devices()
            .map { d ->
                val services = Client.servicesByDevice(d.id)
                EnrichedDevice(d, services)
            }
            .mapNotNull { d -> convertBoschSmartHomeToInflux(d) }
            .flatten()
            .map{p -> p.time(now, WritePrecision.S)}

        InfluxClient.push(points)
        println("${points.size} points sent")
        Thread.sleep(1000 * 60 * ConfigHelper.config.smarthome.interval)
    }
}