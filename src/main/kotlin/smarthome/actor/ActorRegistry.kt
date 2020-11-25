package smarthome.actor

import bsh.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.withContext

typealias ServiceActor = SendChannel<Service>

object ActorRegistry {

    private val actors: MutableCollection<ServiceActor> = mutableListOf()

    fun add(actor: ServiceActor) {
        actors += actor
    }

    suspend fun toAll(service: Service) = withContext(Dispatchers.Default) {
        actors.forEach {
            it.send(service)
        }
    }
}