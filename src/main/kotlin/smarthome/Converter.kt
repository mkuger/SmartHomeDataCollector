package smarthome

import bsh.*
import org.influxdb.dto.Point

fun convertBoschSmartHomeToInflux(device: EnrichedDevice): List<Point>? {
    val result = mutableListOf<Point>()
    device.services.forEach { s ->
        when (s.state) {
            is ValveTappetState -> {
                val point = Point.measurement("valve")
                    .tag("device", device.device.name)
                    .addField("valve", (s.state as ValveTappetState).position)
                result.add(point.build())
            }
            is TemperatureLevelState -> {
                val point = Point.measurement("temperature")
                    .tag("device", device.device.name)
                    .addField("temperature", (s.state as TemperatureLevelState).temperature)
                result.add(point.build())
            }
            is ShutterContactState -> {
                val point = Point.measurement("shutter-state")
                    .tag("device", device.device.name)
                    .addField("shutter-state", (s.state as ShutterContactState).value)
                result.add(point.build())
            }
            is ClimateControlState -> {
                var point = Point.measurement("target-temperature")
                    .tag("device", device.device.name)
                    .addField("target-temperature", (s.state as ClimateControlState).setpointTemperature)
                result.add(point.build())
                point = Point.measurement("climate-operation-mode")
                    .tag("device", device.device.name)
                    .addField("climate-operation-mode", (s.state as ClimateControlState).operationMode)
                result.add(point.build())
            }
        }
    }
    return try {
        result
    } catch (ex: IllegalArgumentException) {
        null
    }
}