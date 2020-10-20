package smarthome

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource
import java.nio.file.Paths

data class Credentials(val user: String, val password: String)
data class BSH(val ip: String, val port: String, val clientCertPassword: String)
data class Influx(val user: String, val password: String, val database: String, val url: String)
data class SmartHome(val interval: Long)
data class Config(val bsh: BSH, val influx: Influx, val smarthome: SmartHome)

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