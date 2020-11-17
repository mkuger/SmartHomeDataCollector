package netatmo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.influxdb.annotations.Column
import java.time.Instant

data class AuthResponse(var access_token: String?, var refresh_token: String, var expires_in: Long?)

data class Station(
    @JsonProperty("_id") var id: String,
    @JsonProperty("dashboard_data") var dashboardData: IndoorMeasurement,
    var type: String,
    var modules: Array<Module>
)

data class Module(
    @JsonProperty("module_name") var name: String,
    @JsonProperty("dashboard_data") var measurement: SatelliteMeasurement,
    var type: String,
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

    abstract var moduleType: String
    abstract var timestamp: Instant
}

sealed class SatelliteMeasurement : Measurement()

@com.influxdb.annotations.Measurement(name = "outdoor")
data class OutdoorWeatherMeasurement(
    @Column @JsonProperty("Temperature") var temperature: Double,
    @Column @JsonProperty("Humidity") var humidity: Double,
    @Column @JsonProperty("temp_trend") var tempTrend: String,
    @Column(timestamp = true) @JsonProperty("time_utc") override var timestamp: Instant
) : SatelliteMeasurement() {
    @Column(tag = true)
    override var moduleType = "NAModule1"
}

@com.influxdb.annotations.Measurement(name = "wind")
data class WindMeasurement(
    @Column @JsonProperty("WindStrength") var windStrength: Double,
    @Column @JsonProperty("WindAngle") var windAngle: Double,
    @Column @JsonProperty("GustStrength") var gustStrength: Double,
    @Column @JsonProperty("GustAngle") var gustAngle: Double,
    @Column(timestamp = true) @JsonProperty("time_utc") override var timestamp: Instant
) : SatelliteMeasurement() {
    @Column(tag = true)
    override var moduleType = "NAModule2"
}

@com.influxdb.annotations.Measurement(name = "rain")
data class RainMeasurement(
    @Column @JsonProperty("Rain") var rain: Double,
    @Column(timestamp = true) @JsonProperty("time_utc") override var timestamp: Instant
) : SatelliteMeasurement() {
    @Column(tag = true)
    override var moduleType = "NAModule3"
}

@com.influxdb.annotations.Measurement(name = "indoor")
data class IndoorMeasurement(
    @Column @JsonProperty("CO2") var co2: Double,
    @Column @JsonProperty("Noise") var noise: Double,
    @Column @JsonProperty("Pressure") var pressure: Double,
    @Column @JsonProperty("pressure_trend") var pressureTrend: String,
    @Column @JsonProperty("Temperature") var temperature: Double,
    @Column @JsonProperty("Humidity") var humidity: Double,
    @Column @JsonProperty("temp_trend") var tempTrend: String,
    @Column(timestamp = true) @JsonProperty("time_utc") override var timestamp: Instant,
) : Measurement() {
    @Column(tag = true)
    override var moduleType = "NAMain"
}

data class StationBody(var devices: Array<Station>)
data class StationResponse(var body: StationBody)

data class Token(val accessToken: String, val refreshToken: String, val expires: Instant)