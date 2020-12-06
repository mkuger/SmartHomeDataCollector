package smarthome

enum class SmartHomeSolution(val bucket: String) {
    BoschSmartHome(ConfigHelper.config.bsh.influxBucket),
    Netatmo(ConfigHelper.config.netatmo.influxBucket)
}