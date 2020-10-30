package smarthome.actor

import bsh.Device
import bsh.RoomRegistry
import bsh.Service
import bsh.ShutterContactState
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import influxdb.InfluxClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor
import java.time.Duration
import java.time.Instant

fun CoroutineScope.shutterActor(device: Device) = actor<Service> {
    println("ShutterActor created: ${device.name}")
    var currentState = ""
    var timestamp = Instant.MIN

    for (msg in channel) { // iterate over incoming messages
        if (msg.state !is ShutterContactState)
            continue
        if (msg.deviceId != device.id)
            continue
        println("Message received: $msg")
        val shutterState = (msg.state as ShutterContactState).value
        when (shutterState) {
            "OPEN" -> {
                currentState = shutterState
                timestamp = Instant.now()
            }
            "CLOSED" -> {
                if (currentState != "OPEN") {
                    println("Skipping. Was not open before.")
                    continue
                }
                val now = Instant.now()
                val duration = Duration.between(timestamp, now)
                    .seconds
                val p = Point("ventilation")
                    .addTag("room", RoomRegistry.byId(device.roomId).name)
                    .addTag("device", device.name)
                    .addTag("from", timestamp.epochSecond.toString())
                    .addTag("until", now.epochSecond.toString())
                    .addField("duration", duration)
                    .time(now, WritePrecision.S)
                println("Sending to influx. Duration: $duration")
                InfluxClient.push(p)
                currentState = shutterState
                timestamp = Instant.now()
            }
        }
    }
}
