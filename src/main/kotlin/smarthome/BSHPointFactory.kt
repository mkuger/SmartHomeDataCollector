package smarthome

import bsh.Device
import bsh.RoomRegistry
import bsh.TemperatureLevelState
import bsh.ValveTappetState
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import java.time.Instant

object BSHPointFactory {

    fun temperaturePoint(device: Device, temperature: TemperatureLevelState): Point {
        return point("temperature", device)
            .addField("temperature", temperature.temperature)
    }

    fun valvePoint(device: Device, valve: ValveTappetState): Point {
        return point("valve", device)
            .addField("valve", valve.position)
    }

    private fun point(measurement: String, device: Device): Point {
        return Point(measurement)
            .addTag("device", device.name)
            .addTag("room", RoomRegistry.byId(device.roomId).name)
            .addTag("solution", "bosch-smart-home")
            .time(Instant.now(), WritePrecision.S)
    }
}