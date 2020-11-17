import netatmo.Module
import netatmo.RainMeasurement
import netatmo.Station
import netatmo.WindMeasurement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class StationTest {

    @Test
    fun deserialize() {
        val json = """{
                "_id": "70:ee:50:6b:46:2c",
                "date_setup": 1603988153,
                "last_setup": 1603988153,
                "type": "NAMain",
                "last_status_store": 1605176882,
                "module_name": "Wetterstation",
                "firmware": 178,
                "wifi_status": 43,
                "reachable": true,
                "co2_calibrating": false,
                "data_type": [
                    "Temperature",
                    "CO2",
                    "Humidity",
                    "Noise",
                    "Pressure"
                ],
                "place": {
                    "altitude": 474,
                    "city": "Landkreis Neu-Ulm",
                    "country": "DE",
                    "timezone": "Europe\/Berlin",
                    "location": [
                        10.009398,
                        48.373667
                    ]
                },
                "home_id": "5f9aeab9f2ae091e166613a3",
                "home_name": "Zuhause",
                "dashboard_data": {
                    "time_utc": 1605176874,
                    "Temperature": 21.7,
                    "CO2": 681,
                    "Humidity": 51,
                    "Noise": 33,
                    "Pressure": 1021.3,
                    "AbsolutePressure": 965.2,
                    "min_temp": 20.4,
                    "max_temp": 21.7,
                    "date_max_temp": 1605173541,
                    "date_min_temp": 1605165698,
                    "temp_trend": "stable",
                    "pressure_trend": "stable"
                },
                "modules": [
                    {
                        "_id": "02:00:00:6b:33:a4",
                        "type": "NAModule1",
                        "module_name": "Outdoor Module",
                        "last_setup": 1603988154,
                        "data_type": [
                            "Temperature",
                            "Humidity"
                        ],
                        "battery_percent": 100,
                        "reachable": true,
                        "firmware": 50,
                        "last_message": 1605176876,
                        "last_seen": 1605176856,
                        "rf_status": 70,
                        "battery_vp": 6140,
                        "dashboard_data": {
                            "time_utc": 1605176856,
                            "Temperature": 8.7,
                            "Humidity": 93,
                            "min_temp": 7,
                            "max_temp": 8.7,
                            "date_max_temp": 1605176549,
                            "date_min_temp": 1605139021,
                            "temp_trend": "stable"
                        }
                    },
                    {
                        "_id": "06:00:00:04:a6:58",
                        "type": "NAModule2",
                        "module_name": "Windmesser",
                        "last_setup": 1603998695,
                        "data_type": [
                            "Wind"
                        ],
                        "battery_percent": 100,
                        "reachable": true,
                        "firmware": 25,
                        "last_message": 1605176876,
                        "last_seen": 1605176876,
                        "rf_status": 120,
                        "battery_vp": 6060,
                        "dashboard_data": {
                            "time_utc": 1605176869,
                            "WindStrength": 14,
                            "WindAngle": 243,
                            "GustStrength": 32,
                            "GustAngle": 237,
                            "max_wind_str": 32,
                            "max_wind_angle": 237,
                            "date_max_wind_str": 1605176869
                        }
                    },
                    {
                        "_id": "05:00:00:08:3d:a8",
                        "type": "NAModule3",
                        "module_name": "Regenmesser",
                        "last_setup": 1603999261,
                        "data_type": [
                            "Rain"
                        ],
                        "battery_percent": 100,
                        "reachable": true,
                        "firmware": 12,
                        "last_message": 1605176876,
                        "last_seen": 1605176869,
                        "rf_status": 62,
                        "battery_vp": 6174,
                        "dashboard_data": {
                            "time_utc": 1605176869,
                            "Rain": 0,
                            "sum_rain_1": 0,
                            "sum_rain_24": 0.1
                        }
                    }
                ]
            }
        ],
        "user": {
            "mail": "michael@mikuger.de",
            "administrative": {
                "lang": "de-DE",
                "reg_locale": "de-DE",
                "country": "DE",
                "unit": 0,
                "windunit": 0,
                "pressureunit": 0,
                "feel_like_algo": 0
            }
        }
        }
"""
        val station = GlobalConfig.jsonMapper.readValue(json, Station::class.java)
        assertEquals(681.0, station.dashboardData.co2)
        val rainMeasurement = RainMeasurement(0.0, Instant.ofEpochSecond(1605176869))
        val rainModule = Module("Regenmesser", rainMeasurement, "NAModule3")
        val windMeasurement = WindMeasurement(14.0, 243.0, 32.0, 237.0, Instant.ofEpochSecond(1605176869))
        val windModule = Module("Windmesser", windMeasurement, "NAModule2")
        assertTrue(rainModule in station.modules)
        assertTrue(windModule in station.modules)
    }
}