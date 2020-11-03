package bsh

import mu.KotlinLogging
import java.time.Instant

object RoomRegistry {
    private val log = KotlinLogging.logger {}

    var rooms: Array<Room> = emptyArray()
        set(value) {
            lastUpdated = Instant.now()
            field = value
            log.debug("Rooms updated")
        }

    var lastUpdated: Instant = Instant.MIN
        private set

    fun byId(id: String): Room {
        return rooms.first { room -> room.id == id }
    }
}