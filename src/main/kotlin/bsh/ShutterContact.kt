package bsh

import bsh.client.LongPollingClient
import bsh.jsonrpc.LongPollingResponse

object ShutterContact : LongPollingClient.EventHandler {

    override fun receive(response: LongPollingResponse) {
        TODO("Not yet implemented")
    }
}