package smarthome

import bsh.*
import com.influxdb.client.write.Point

fun convertBoschSmartHomeToInflux(device: EnrichedDevice): List<Point>? {
    val result = mutableListOf<Point>()
    device.services.forEach { s ->
        when (s.state) {
            is ValveTappetState -> {
                val point = Point.measurement("valve")
                    .addTag("device", device.device.name)
                    .addField("valve", (s.state as ValveTappetState).position)
                result.add(point)
            }
            is TemperatureLevelState -> {
                val point = Point.measurement("temperature")
                    .addTag("device", device.device.name)
                    .addField("temperature", (s.state as TemperatureLevelState).temperature)
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
    result.map { p -> p.addTag("room", device.device.roomId) }
    return try {
        result
    } catch (ex: IllegalArgumentException) {
        null
    }
}