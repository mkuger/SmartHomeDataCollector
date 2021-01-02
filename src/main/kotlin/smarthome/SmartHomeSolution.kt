package smarthome

import bsh.BSHSubsystem
import netatmo.NetatmoSubsystem

enum class SmartHomeSolution(val bucket: String) {
    BoschSmartHome(BSHSubsystem.config!!.influxBucket),
    Netatmo(NetatmoSubsystem.config!!.influxBucket)
}