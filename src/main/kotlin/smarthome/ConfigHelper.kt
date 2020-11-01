package smarthome

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource
import java.nio.file.Paths

data class BSH(val ip: String, val port: String, val clientCertPassword: String)
data class Influx(val token: String, val bucket: String, val org: String, val url: String)
data class SmartHome(val interval: Long)
data class Grafana(val url: String, val apiKey: String)
data class Config(val bsh: BSH, val influx: Influx, val smarthome: SmartHome, val grafana: Grafana)

object ConfigHelper {

    val appHome = Paths.get(System.getProperty("user.home")).resolve(".smart-home")
    val config: Config

    init {
        val configfile = appHome.resolve("config.yaml")
        config = ConfigLoader.Builder()
            .addSource(PropertySource.path(configfile))
            .build()
            .loadConfigOrThrow()
    }
}