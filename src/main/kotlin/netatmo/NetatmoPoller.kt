package netatmo

import influxdb.InfluxClient
import mu.KotlinLogging
import smarthome.ConfigHelper

object NetatmoPoller : Runnable {
    private val log = KotlinLogging.logger {}
    private val config = ConfigHelper.config.netatmo

    override fun run() {
        try {
            log.info("Starting Netatmo Query")
            val station = Client.queryStation()
            InfluxClient.push(station.dashboardData, config.influxBucket)
            for (module in station.modules)
                InfluxClient.push(module.measurement, config.influxBucket)
            log.info("Finished Netatmo Query")
        } catch (e: Throwable) {
            log.warn("Exception while polling Netatmo", e)
        }
    }
}