package bsh

import bsh.client.Client
import com.influxdb.client.domain.WritePrecision
import influxdb.InfluxClient
import mu.KotlinLogging
import smarthome.SmartHomeSolution
import smarthome.convertBoschSmartHomeToInflux
import java.time.Instant
import java.util.*

object BSHPoller : TimerTask() {
    val log = KotlinLogging.logger {}

    override fun run() {
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

        InfluxClient.push(points, SmartHomeSolution.BoschSmartHome)
        log.info("${points.size} points sent")
    }
}