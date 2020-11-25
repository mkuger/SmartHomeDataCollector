package bsh.client

data class Request private constructor(val method: String, val params: Collection<Any?>) {
    companion object {
        fun subscribe(): Request {
            return Request("RE/subscribe", listOf("com/bosch/sh/remote/*", null))
        }

        fun unsubscribe(longPollingId: String?): Request {
            return Request("RE/unsubscribe", listOf(longPollingId))
        }

        fun longpolling(longPollingId: String?): Request {
            return Request("RE/longPoll", listOf(longPollingId, 30))
        }

    }

    val jsonrpc = "2.0"
}