package bsh

import java.time.Instant

object DeviceRegistry {

    var devices: Array<Device> = emptyArray()
        set(value) {
            lastUpdated = Instant.now()
            field = value
        }

    fun deviceById(id: String) {
        devices.filter { d -> d.id == id }
    }

    var lastUpdated: Instant = Instant.MIN
        private set


}