import bsh.Service
import bsh.TemperatureLevelState
import bsh.ValveTappetState
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import smarthome.actor.ActorBuilder

class ActorBuilderTest {

    val valveService = Service(id = "0815", state = ValveTappetState(23), deviceId = "0711")
    val temperatureLevelService = Service(id = "564564", state = TemperatureLevelState(5.4), deviceId = "98751")

    @Test
    fun foo() {
        var valvePosition = 1
        val receivedEvents = mutableListOf<Service>()
        val actor = ActorBuilder()
                .filterFor(listOf(ValveTappetState::class))
                .handler { service ->
                    assertEquals(ValveTappetState::class, service.state!!::class)
                    receivedEvents += service
                    val valveState = service.state as ValveTappetState
                    valvePosition = valveState.position
                }
                .build()
        runBlocking {
            actor.send(valveService)
            actor.send(temperatureLevelService)
        }
        assertEquals(23, valvePosition)
        assertEquals(1, receivedEvents.size)
    }
}