package bsh

object DeviceRegistry {

    var devices: Array<Device> = emptyArray()

    fun deviceById(id: String) {
        devices.filter { d -> d.id == id }
    }
}