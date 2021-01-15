package netatmo

import influxdb.InfluxClient
import mu.KotlinLogging
import smarthome.SmartHomeSolution

object NetatmoPoller : Runnable {
    private val log = KotlinLogging.logger {}

    override fun run() {
        try {
            log.info("Starting Netatmo Query")
            val station = Client.queryStation()
            InfluxClient.push(station.dashboardData, SmartHomeSolution.Netatmo)
            for (module in station.modules) {
                val measurement = module.measurement
                if (measurement == null) {
                    log.info("No measurement for module $module")
                    continue
                }
                InfluxClient.push(measurement, SmartHomeSolution.Netatmo)
            }
            log.info("Finished Netatmo Query")
        } catch (e: Throwable) {
            log.warn("Exception while polling Netatmo", e)
        }
    }
}