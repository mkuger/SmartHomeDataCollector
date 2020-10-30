package influxdb

import com.influxdb.client.InfluxDBClientFactory
import com.influxdb.client.write.Point
import smarthome.ConfigHelper

object InfluxClient {
    private val config = ConfigHelper.config.influx

    fun push(points: List<Point>) {
        val influxDBClient = InfluxDBClientFactory
            .create(config.url, config.token.toCharArray(), config.org, config.bucket)

        val writeApi = influxDBClient.writeApi
        writeApi.writePoints(points)
        influxDBClient.close()
    }

    fun push(point: Point) {
        val influxDBClient = InfluxDBClientFactory
            .create(config.url, config.token.toCharArray(), config.org, config.bucket)

        val writeApi = influxDBClient.writeApi
        writeApi.writePoint(point)
        influxDBClient.close()
    }
}