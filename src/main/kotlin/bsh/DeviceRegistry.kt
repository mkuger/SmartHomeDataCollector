package bsh

import mu.KotlinLogging
import java.time.Instant

object DeviceRegistry {
    private val log = KotlinLogging.logger {}

    var devices: Array<Device> = emptyArray()
        set(value) {
            lastUpdated = Instant.now()
            field = value
            log.debug("Devices updated")
        }

    fun deviceById(id: String) {
        devices.filter { d -> d.id == id }
    }

    var lastUpdated: Instant = Instant.MIN
        private set


}