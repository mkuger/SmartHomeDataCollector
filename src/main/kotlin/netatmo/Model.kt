package netatmo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.influxdb.annotations.Column
import java.time.Instant

data class AuthResponse(val access_token: String?, val refresh_token: String, val expires_in: Long?)

data class Station(
    @JsonProperty("_id") val id: String,
    @JsonProperty("dashboard_data") val dashboardData: IndoorMeasurement,
    val type: String,
    val modules: Array<Module>
)

data class Module(
    @JsonProperty("module_name") val name: String,
    @JsonProperty("dashboard_data") val measurement: SatelliteMeasurement?,
    val type: String,
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.DEDUCTION
)
@JsonSubTypes(
    JsonSubTypes.Type(OutdoorWeatherMeasurement::class),
    JsonSubTypes.Type(RainMeasurement::class),
    JsonSubTypes.Type(WindMeasurement::class)
)

sealed class Measurement {
    abstract val moduleType: String
    abstract val timestamp: Instant
}

sealed class SatelliteMeasurement : Measurement()

@com.influxdb.annotations.Measurement(name = "outdoor")
data class OutdoorWeatherMeasurement(
    @Column @JsonProperty("Temperature") val temperature: Double,
    @Column @JsonProperty("Humidity") val humidity: Double,
    @Column @JsonProperty("temp_trend") val tempTrend: String,
    @Column(timestamp = true) @JsonProperty("time_utc") override val timestamp: Instant
) : SatelliteMeasurement() {
    @Column(tag = true)
    override val moduleType = "NAModule1"
}

@com.influxdb.annotations.Measurement(name = "wind")
data class WindMeasurement(
    @Column @JsonProperty("WindStrength") val windStrength: Double,
    @Column @JsonProperty("WindAngle") val windAngle: Double,
    @Column @JsonProperty("GustStrength") val gustStrength: Double,
    @Column @JsonProperty("GustAngle") val gustAngle: Double,
    @Column(timestamp = true) @JsonProperty("time_utc") override val timestamp: Instant
) : SatelliteMeasurement() {
    @Column(tag = true)
    override val moduleType = "NAModule2"
}

@com.influxdb.annotations.Measurement(name = "rain")
data class RainMeasurement(
    @Column @JsonProperty("Rain") val rain: Double,
    @Column(timestamp = true) @JsonProperty("time_utc") override val timestamp: Instant
) : SatelliteMeasurement() {
    @Column(tag = true)
    override val moduleType = "NAModule3"
}

@com.influxdb.annotations.Measurement(name = "indoor")
data class IndoorMeasurement(
    @Column @JsonProperty("CO2") val co2: Double,
    @Column @JsonProperty("Noise") val noise: Double,
    @Column @JsonProperty("Pressure") val pressure: Double,
    @Column @JsonProperty("pressure_trend") val pressureTrend: String,
    @Column @JsonProperty("Temperature") val temperature: Double,
    @Column @JsonProperty("Humidity") val humidity: Double,
    @Column @JsonProperty("temp_trend") val tempTrend: String,
    @Column(timestamp = true) @JsonProperty("time_utc") override val timestamp: Instant,
) : Measurement() {
    @Column(tag = true)
    override val moduleType = "NAMain"
}

data class StationBody(val devices: Array<Station>)
data class StationResponse(val body: StationBody)

data class Token(val accessToken: String, val refreshToken: String, val expires: Instant)