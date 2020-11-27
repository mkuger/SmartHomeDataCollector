package smarthome.actor

import bsh.Service
import bsh.ServiceState
import bsh.ValveTappetState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import kotlin.reflect.KClass

class ActorBuilder {
    private var events: List<KClass<out ServiceState>> = emptyList()
    private var handler: (Service) -> Unit = { }

    fun filterFor(events: List<KClass<out ServiceState>>): ActorBuilder {
        this.events = events
        return this
    }

    fun handler(handler: (Service) -> Unit): ActorBuilder {
        this.handler = handler
        filterFor(listOf(ValveTappetState::class))
        return this
    }

    fun build(): ServiceActor {
        return GlobalScope.actor<Service> {
            for (msg in channel) {
                if (events.isNotEmpty() && msg.state!!::class !in events) {
                    continue
                }
                handler.invoke(msg)
            }
        }
    }
}