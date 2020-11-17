package smarthome

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource
import java.nio.file.Paths

data class BSH(val ip: String, val port: String, val clientCertPassword: String, val influxBucket: String)
data class Influx(val token: String, val org: String, val url: String)
data class Grafana(val url: String, val apiKey: String)
data class Netatmo(
    val user: String,
    val password: String,
    val clientID: String,
    val clientSecret: String,
    val influxBucket: String
)

data class Config(
    val bsh: BSH,
    val influx: Influx,
    val grafana: Grafana,
    val netatmo: Netatmo,
)

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