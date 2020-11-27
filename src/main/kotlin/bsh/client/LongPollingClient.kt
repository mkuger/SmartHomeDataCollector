package bsh.client

import GlobalConfig
import bsh.jsonrpc.LongPollingResponse
import bsh.jsonrpc.SubscribeResponse
import bsh.jsonrpc.UnsubscribeResponse
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.jackson.responseObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import smarthome.actor.ActorRegistry
import kotlin.concurrent.thread

fun main() {
    LongPollingClient.subscribe()
    LongPollingClient.startPolling()
    Thread.sleep(5000)
    LongPollingClient.unsubscribe()
}

object LongPollingClient {
    private val log = KotlinLogging.logger {}
    private const val url = "/remote/json-rpc"

    private var longPollingId: String? = null

    init {
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                log.info("Received shutdown hook. Unsubscribing from long polling.")
                unsubscribe()
            }
        })
    }

    fun subscribe() {
        val requestBody = GlobalConfig.jsonMapper.writeValueAsString(listOf(Request.subscribe()))
        val request = Client.fuelManager.post(url)
                .jsonBody(requestBody)
                .responseObject<Array<SubscribeResponse>>()
        longPollingId = request.third.get()[0].result
        log.info("Subscribed. Id: ${longPollingId}")
    }

    fun unsubscribe() {
        if (longPollingId == null) {
            log.warn("Skipping unsubscribe, currently not subscribed")
            return
        }
        val requestBody = GlobalConfig.jsonMapper.writeValueAsString(listOf(Request.unsubscribe(longPollingId)))
        val response = Client.fuelManager.post(url)
                .jsonBody(requestBody)
                .responseObject<Array<UnsubscribeResponse>>()
        log.info("Unsubscribe response: ${response.second.statusCode} : ${response.second.responseMessage}")
        longPollingId = null
    }

    fun startPolling() {
        thread(start = true, isDaemon = true) {
            while (true) {
                try {
                    if (longPollingId == null) {
                        log.warn("Long polling ID is null. Skipping...")
                        Thread.sleep(1000L)
                        continue
                    }
                    val requestBody = GlobalConfig.jsonMapper.writeValueAsString(listOf(Request.longpolling(longPollingId)))
                    val response = Client.fuelManager.post(url)
                            .jsonBody(requestBody).responseObject<Array<LongPollingResponse>>()
                            .third.get()
                    if (response.isEmpty()) {
                        continue
                    }
                    if (response[0].error != null) {
                        log.warn("Error during longpoll. Re-subscribing...")
                        unsubscribe()
                        subscribe()
                        continue
                    }
                    log.debug("${response[0].result?.size} message(s) received:")
                    response.forEach { r ->
                        r.result?.forEach { s ->
                            log.debug("\t${s.state?.javaClass}")
                        }
                    }
                    val exceptionHandler = CoroutineExceptionHandler { _, throwable -> log.warn("Could not handle long polling response", throwable) }
                    GlobalScope.launch(context = exceptionHandler) {
                        response.forEach {
                            it.result?.forEach { s ->
                                ActorRegistry.toAll(s)
                            }
                        }
                    }
                } catch (e: Throwable) {
                    log.warn("Long polling failed", e)
                }
            }
        }
    }
}