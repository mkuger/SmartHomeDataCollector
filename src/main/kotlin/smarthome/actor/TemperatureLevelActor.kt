package smarthome.actor

import bsh.Device
import bsh.Service
import bsh.TemperatureLevelState
import influxdb.InfluxClient
import mu.KotlinLogging
import smarthome.BSHPointFactory
import smarthome.SmartHomeSolution

class TemperatureLevelActor private constructor(private val device: Device) : ServiceHandler {
    private val log = KotlinLogging.logger {}

    init {
        log.info("${device.id} created")
    }

    override fun handle(service: Service) {
        if (device.id != service.deviceId) {
            return
        }
        val state = service.state as TemperatureLevelState
        val point = BSHPointFactory.temperaturePoint(device, state)
        InfluxClient.push(point, SmartHomeSolution.BoschSmartHome)
    }

    companion object {
        fun instance(device: Device): ServiceActor {
            val logic = TemperatureLevelActor(device)
            return ActorBuilder()
                .filterFor(TemperatureLevelState::class)
                .handler(logic)
                .build()
        }
    }
}