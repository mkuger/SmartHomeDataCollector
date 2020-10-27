package bsh

import java.time.Instant

object ServiceRegistry {

    var services: Array<Service> = emptyArray()
        set(value) {
            lastUpdated = Instant.now()
            field = value
        }

    var lastUpdated: Instant = Instant.MIN
        private set

    fun servicesByDevice(device: Device): Collection<Service> {
        return services
            .filter { s -> s.deviceId == device.id }
    }
}