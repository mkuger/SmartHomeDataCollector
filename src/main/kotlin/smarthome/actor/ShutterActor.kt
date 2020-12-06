package smarthome.actor

import bsh.Device
import bsh.RoomRegistry
import bsh.Service
import bsh.ShutterContactState
import grafana.Annotation
import grafana.GrafanaClient
import mu.KotlinLogging
import java.time.Instant


class ShutterActor private constructor(private val device: Device) : ServiceHandler {
    private val log = KotlinLogging.logger {}
    private var currentState = ""
    private var timestamp = Instant.MIN

    init {
        log.info("${device.id} created")
    }

    override fun handle(service: Service) {
        if (service.deviceId != device.id)
            return
        log.info("Message received: $service")
        when (val shutterState = (service.state as ShutterContactState).value) {
            "OPEN" -> {
                currentState = shutterState
                timestamp = Instant.now()
            }
            "CLOSED" -> {
                if (currentState != "OPEN") {
                    log.info("Skipping. Was not open before.")
                    return
                }
                val now = Instant.now()
                val tags = listOf(RoomRegistry.byId(device.roomId).name, device.name, "smart-home", "ventilation")
                val annotation = Annotation(timestamp.toEpochMilli(), now.toEpochMilli(), tags, "ventilation")
                log.debug("Sending event to Grafana")
                GrafanaClient.addAnnotation(annotation)
                currentState = shutterState
                timestamp = now
            }
        }
    }

    companion object {
        fun instance(device: Device): ServiceActor {
            val logic = ShutterActor(device)
            return ActorBuilder()
                    .filterFor(ShutterContactState::class)
                    .handler(logic)
                    .build()
        }
    }
}