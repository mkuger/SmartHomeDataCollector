package bsh.jsonrpc

import bsh.Service

data class Error(val code: Double, val message: String)
data class SubscribeResponse(val result: String?, val error: Error?)
data class UnsubscribeResponse(val result: String?, val error: Error?)
data class LongPollingResponse(val result: Array<Service>?, val error: Error?)