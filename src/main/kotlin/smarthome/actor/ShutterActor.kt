package smarthome.actor

import bsh.Device
import bsh.RoomRegistry
import bsh.Service
import bsh.ShutterContactState
import grafana.Annotation
import grafana.GrafanaClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor
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
                val tags = listOf(RoomRegistry.byId(device.roomId).name, device.name, "smart-home", "ventilation")
                val annotation = Annotation(timestamp.toEpochMilli(), now.toEpochMilli(), tags, "ventilation")
                GrafanaClient.addAnnotation(annotation)
                println("Sending to Grafana")
                currentState = shutterState
                timestamp = now
            }
        }
    }
}
