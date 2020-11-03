package bsh

import mu.KotlinLogging
import java.time.Instant

object ServiceRegistry {
    private val log = KotlinLogging.logger {}

    var services: Array<Service> = emptyArray()
        set(value) {
            lastUpdated = Instant.now()
            field = value
            log.debug("services updated")
        }

    var lastUpdated: Instant = Instant.MIN
        private set

    fun servicesByDevice(device: Device): Collection<Service> {
        return services
            .filter { s -> s.deviceId == device.id }
    }
}