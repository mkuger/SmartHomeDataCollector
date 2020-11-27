package smarthome.actor

import bsh.Service
import bsh.ServiceState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import mu.KotlinLogging
import kotlin.reflect.KClass

class ActorBuilder {
    private val log = KotlinLogging.logger {}
    private var events: MutableList<KClass<out ServiceState>> = ArrayList()
    private var handler = ServiceHandler {}

    fun filterFor(events: List<KClass<out ServiceState>>): ActorBuilder {
        this.events.addAll(events)
        return this
    }

    fun filterFor(event: KClass<out ServiceState>): ActorBuilder {
        events.add(event)
        return this
    }

    fun handler(handler: ServiceHandler): ActorBuilder {
        this.handler = handler
        return this
    }

    fun build(): ServiceActor {
        return GlobalScope.actor<Service> {
            for (msg in channel) {
                try {
                    if (events.isNotEmpty() && msg.state!!::class !in events) {
                        continue
                    }
                    handler.handle(msg)
                } catch (e: Throwable) {
                    log.warn("Could not handle message", e)
                }
            }
        }
    }
}