package bsh

import java.time.Instant

object RoomRegistry {
    var rooms: Array<Room> = emptyArray()
        set(value) {
            lastUpdated = Instant.now()
            field = value
        }

    var lastUpdated: Instant = Instant.MIN
        private set

    fun byId(id: String): Room {
        return rooms.first { room -> room.id == id }
    }
}