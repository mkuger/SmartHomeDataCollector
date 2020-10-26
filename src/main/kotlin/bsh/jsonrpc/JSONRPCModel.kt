package bsh.jsonrpc

import bsh.Service

data class Error(var code: Double, var message: String)
data class SubscribeResponse(var result: String?, var error: Error?)
data class UnsubscribeResponse(var result: String?, var error: Error?)
data class LongPollingResponse(var result: Array<Service>?, var error: Error?)