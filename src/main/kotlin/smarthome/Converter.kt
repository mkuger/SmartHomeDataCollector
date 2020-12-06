package smarthome

import bsh.*
import com.influxdb.client.write.Point

fun convertBoschSmartHomeToInflux(device: EnrichedDevice): List<Point>? {
    val result = mutableListOf<Point>()
    device.services.forEach { s ->
        when (s.state) {
            is ValveTappetState -> {
                val point = BSHPointFactory.valvePoint(device.device, s.state as ValveTappetState)
                result.add(point)
            }
            is TemperatureLevelState -> {
                val point = BSHPointFactory.temperaturePoint(device.device, s.state as TemperatureLevelState)
                result.add(point)
            }
            is ShutterContactState -> {
                val point = Point.measurement("shutter-state")
                    .addTag("device", device.device.name)
                    .addField("shutter-state", (s.state as ShutterContactState).value)
                result.add(point)
            }
            is ClimateControlState -> {
                var point = Point.measurement("target-temperature")
                    .addTag("device", device.device.name)
                    .addField("target-temperature", (s.state as ClimateControlState).setpointTemperature)
                result.add(point)
                point = Point.measurement("climate-operation-mode")
                    .addTag("device", device.device.name)
                    .addField("climate-operation-mode", (s.state as ClimateControlState).operationMode)
                result.add(point)
            }
        }
    }
    result.map { p ->
        p.addTag("room", RoomRegistry.byId(device.device.roomId).name)
            .addTag("solution", "bosch-smart-home")
    }
    return result
}