package grafana

import GlobalConfig
import com.github.kittinunf.fuel.core.FuelManager
import smarthome.ConfigHelper

data class Annotation(val time: Long, val timeEnd: Long? = null, val tags: List<String>, val text: String)

object GrafanaClient {

    private val fuelManager: FuelManager = FuelManager()

    init {
        fuelManager.apply {
            basePath = ConfigHelper.config.grafana.url
            baseHeaders = mapOf("Content-Type" to "application/json; charset=utf-8")
        }
    }

    fun addAnnotation(annotation: Annotation) {
        val mapper = GlobalConfig.jsonMapper
        val response = fuelManager
            .post("/api/annotations")
            .header("Authorization" to "Bearer ${ConfigHelper.config.grafana.apiKey}")
            .body(mapper.writeValueAsString(annotation))
            .responseString()
        println("Grafana response: ${response.second.statusCode} - ${response.second.responseMessage}")
    }
}