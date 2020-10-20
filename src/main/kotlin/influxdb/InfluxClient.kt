package influxdb

import org.influxdb.BatchOptions
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.BatchPoints
import org.influxdb.dto.Point
import smarthome.ConfigHelper

object InfluxClient {

    fun push(points: List<Point>) {
        val config = ConfigHelper.config.influx
        val influxDB: InfluxDB =
            InfluxDBFactory.connect(config.url, config.user, config.password)
        influxDB.setDatabase(config.database)
        influxDB.enableBatch(BatchOptions.DEFAULTS)
        val batchPoints = BatchPoints
            .database(config.database)
            .build()
        points.forEach { batchPoints.point(it) }
        println(batchPoints.lineProtocol())
        influxDB.write(batchPoints)
        influxDB.close()
    }
}