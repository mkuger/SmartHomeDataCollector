package smarthome

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ConfigResult
import com.sksamuel.hoplite.EnvironmentVariablesPropertySource
import com.sksamuel.hoplite.PropertySource
import com.sksamuel.hoplite.fp.getOrElse
import java.nio.file.Paths

data class Influx(val token: String, val org: String, val url: String)
data class Grafana(val url: String, val apiKey: String)

data class Config(
    val influx: Influx,
    val grafana: Grafana
)

object ConfigHelper {

    val appHome = Paths.get(System.getProperty("user.home")).resolve(".smart-home")
    val config: Config
    val configfile = appHome.resolve("config.yaml")

    init {
        config = loadConfig<Config>().getOrElse {
            Config(Influx("", "", ""), Grafana("", ""))
        }
    }

    inline fun <reified T> loadConfig(): ConfigResult<T> {
        return ConfigLoader.Builder()
            .addSource(
                EnvironmentVariablesPropertySource(
                    useUnderscoresAsSeparator = false,
                    allowUppercaseNames = true
                )
            )
            .addSource(PropertySource.path(configfile))
            .build()
            .loadConfig()
    }
}