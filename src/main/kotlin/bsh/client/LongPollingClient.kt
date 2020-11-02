package bsh.client

import bsh.jsonrpc.LongPollingResponse
import bsh.jsonrpc.SubscribeResponse
import bsh.jsonrpc.UnsubscribeResponse
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.jackson.responseObject
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import smarthome.actor.ActorRegistry
import kotlin.concurrent.thread

object LongPollingClient {

    private val log = KotlinLogging.logger {}

    interface EventHandler {
        fun receive(response: LongPollingResponse)
    }

    private var longPollingId: String? = null
    val eventHandlers: MutableList<EventHandler> = mutableListOf()

    init {
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                log.info("Received shutdown hook. Unsubscribing from long polling.")
                longPollingUnsubscribe();
            }
        })
    }

    fun subscribe() {
        val url = "/remote/json-rpc"
        val request = Client.fuelManager.post(url)
            .jsonBody(
                "[\n" +
                        "    {\n" +
                        "        \"jsonrpc\":\"2.0\",\n" +
                        "        \"method\":\"RE/subscribe\",\n" +
                        "        \"params\": [\"com/bosch/sh/remote/*\", null]\n" +
                        "    }\n" +
                        "]"
            )
            .responseObject<Array<SubscribeResponse>>()
        longPollingId = request.third.get()[0].result
        log.info("Subscribed. Id: ${longPollingId}")
    }

    fun longPollingUnsubscribe() {
        if (longPollingId == null) {
            log.warn("Skipping unsubscribe, currently not subscribed")
            return
        }
        val url = "/remote/json-rpc"
        val request = Client.fuelManager.post(url)
            .jsonBody(
                "[\n" +
                        "    {\n" +
                        "        \"jsonrpc\":\"2.0\",\n" +
                        "        \"method\":\"RE/unsubscribe\",\n" +
                        "        \"params\": [\"$longPollingId\"] " +
                        "    }\n" +
                        "]"
            )
            .responseObject<Array<UnsubscribeResponse>>()
        request.third.get()[0]
        longPollingId = null
    }

    fun startPolling() {
        thread(start = true, isDaemon = true) {
            while (true) {
                if (longPollingId == null) {
                    log.warn("Long polling ID is null. Skipping...")
                    Thread.sleep(1000L)
                    continue
                }
                log.info("Polling...")
                val url = "/remote/json-rpc"
                val request = Client.fuelManager.post(url)
                    .jsonBody(
                        "[\n" +
                                "    {\n" +
                                "        \"jsonrpc\":\"2.0\",\n" +
                                "        \"method\":\"RE/longPoll\",\n" +
                                "        \"params\": [\"$longPollingId\", 30]\n" +
                                "    }\n" +
                                "]"
                    ).responseObject<Array<LongPollingResponse>>()
                val response = request.third.get()
                runBlocking {
                    response.forEach {
                        eventHandlers.forEach { handler -> handler.receive(it) }
                        it.result?.forEach { service ->
                            ActorRegistry.actors.forEach { actor ->
                                actor.send(service)
                            }
                        }
                        log.info("Results: ${it.result?.size}")
                    }
                }
            }
        }
    }
}