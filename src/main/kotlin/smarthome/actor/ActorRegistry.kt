package smarthome.actor

import bsh.Service
import kotlinx.coroutines.channels.SendChannel

object ActorRegistry {

    val actors: MutableCollection<SendChannel<Service>> = mutableListOf()

    fun add(actor: SendChannel<Service>) {
        actors.add(actor)
    }

    suspend fun toAll(service: Service) {
        actors.forEach {
            it.send(service)
        }
    }
}