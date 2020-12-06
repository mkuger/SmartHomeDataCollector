package smarthome.actor

import bsh.Device
import bsh.Service
import bsh.ValveTappetState
import influxdb.InfluxClient
import mu.KotlinLogging
import smarthome.BSHPointFactory
import smarthome.SmartHomeSolution

class ValveTappetActor private constructor(val device: Device) : ServiceHandler {
    private val log = KotlinLogging.logger {}

    init {
        log.info("${device.id} created")
    }

    override fun handle(service: Service) {
        if (device.id != service.deviceId) {
            return
        }
        val state = service.state as ValveTappetState
        val point = BSHPointFactory.valvePoint(device, state)
        InfluxClient.push(point, SmartHomeSolution.BoschSmartHome)
    }

    companion object {
        fun instance(device: Device): ServiceActor {
            val logic = ValveTappetActor(device)
            return ActorBuilder()
                .filterFor(ValveTappetState::class)
                .handler(logic)
                .build()
        }
    }
}