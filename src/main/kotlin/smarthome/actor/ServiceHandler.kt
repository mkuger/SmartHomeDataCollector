package smarthome.actor

import bsh.Service

fun interface ServiceHandler {

    fun handle(service: Service)
}