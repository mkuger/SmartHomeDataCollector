package influxdb

import com.influxdb.client.InfluxDBClientFactory
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import com.influxdb.client.write.events.BackpressureEvent
import com.influxdb.client.write.events.WriteErrorEvent
import com.influxdb.client.write.events.WriteRetriableErrorEvent
import mu.KotlinLogging
import smarthome.ConfigHelper

object InfluxClient {
    private val log = KotlinLogging.logger {}
    private val config = ConfigHelper.config.influx
    private val client = InfluxDBClientFactory.create(config.url, config.token.toCharArray())
    private val writeApi = client.writeApi

    init {
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                log.info("Received shutdown hook. Closing InfluxDB client")
                client.close()
            }
        })
        writeApi.listenEvents(BackpressureEvent::class.java) {
            log.warn("Influx backpressure")
            it.logEvent()
        }
        writeApi.listenEvents(WriteErrorEvent::class.java) {
            log.warn("Influx write error", it.throwable)
        }
        writeApi.listenEvents(WriteRetriableErrorEvent::class.java) {
            log.warn("Influx write retriable error", it.throwable)
            it.logEvent()
        }
    }

    fun push(points: List<Point>, bucket: String) {
        log.info("Pushing ${points.size} points to Influx. Bucket: $bucket")

        writeApi.writePoints(bucket, config.org, points)
    }

    fun push(pojo: Any, bucket: String) {
        log.info("Pushing POJO measurement ${pojo.javaClass.simpleName} to Influx. Bucket: $bucket")

        writeApi.writeMeasurement(bucket, config.org, WritePrecision.S, pojo)
    }
}