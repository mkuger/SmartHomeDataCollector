package bsh

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

data class EnrichedDevice(val device: Device, val services: Collection<Service>)

data class Room(val id: String = "", val name: String = "")

data class Device(
    val name: String = "",
    val id: String = "",
    val roomId: String = "",
    val status: String = "",
    val deviceServiceIds: Array<String> = emptyArray()
)

data class Service(val id: String, val state: ServiceState?, val deviceId: String)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ValveTappetState::class, name = "valveTappetState"),
    JsonSubTypes.Type(value = TemperatureLevelState::class, name = "temperatureLevelState"),
    JsonSubTypes.Type(value = LinkingState::class, name = "linkingState"),
    JsonSubTypes.Type(value = ChildLockState::class, name = "childLockState"),
    JsonSubTypes.Type(value = TemperatureOffsetState::class, name = "temperatureOffsetState"),
    JsonSubTypes.Type(value = TemperatureLevelConfigurationState::class, name = "temperatureLevelConfigurationState"),
    JsonSubTypes.Type(value = ClimateControlState::class, name = "climateControlState"),
    JsonSubTypes.Type(value = SurveillanceAlarmState::class, name = "surveillanceAlarmState"),
    JsonSubTypes.Type(value = IntrusionDetectionControlState::class, name = "intrusionDetectionControlState"),
    JsonSubTypes.Type(value = KeypadTriggerState::class, name = "keypadTriggerState"),
    JsonSubTypes.Type(value = VentilationDelayState::class, name = "ventilationDelayState"),
    JsonSubTypes.Type(value = ShutterContactState::class, name = "shutterContactState"),
    JsonSubTypes.Type(value = HueBridgeConnectorState::class, name = "hueBridgeConnectorState"),
    JsonSubTypes.Type(value = HueBridgeSearcherState::class, name = "hueBridgeSearcherState"),
    JsonSubTypes.Type(value = SoftwareUpdateState::class, name = "softwareUpdateState"),
    JsonSubTypes.Type(value = RemoteAccessState::class, name = "remoteAccessState"),
    JsonSubTypes.Type(value = RemotePushNotificationState::class, name = "remotePushNotificationState"),
    JsonSubTypes.Type(value = ArmDisarmPushNotificationState::class, name = "armDisarmPushNotificationState"),
)
abstract class ServiceState

abstract class MeasuredServiceState : ServiceState()

data class ValveTappetState(val position: Int) : MeasuredServiceState()

data class TemperatureLevelState(val temperature: Double) : MeasuredServiceState()
data class TemperatureOffsetState(val offset: Double, val stepSize: Double) : ServiceState()
data class ChildLockState(val childLock: String) : ServiceState()
data class ShutterContactState(val value: String) : MeasuredServiceState()
data class ClimateControlState(
    val setpointTemperature: Double,
    val operationMode: String,
    val low: Boolean,
    val boostMode: Boolean,
    val summerMode: Boolean,
    val setpointTemperatureForLevelEco: Double,
    val setpointTemperatureForLevelComfort: Double
) : MeasuredServiceState()

class LinkingState : ServiceState()
class TemperatureLevelConfigurationState : ServiceState()
class SurveillanceAlarmState : ServiceState()
class IntrusionDetectionControlState : ServiceState()
class KeypadTriggerState : ServiceState()
class VentilationDelayState : ServiceState()
class HueBridgeConnectorState : ServiceState()
class HueBridgeSearcherState : ServiceState()
class SoftwareUpdateState : ServiceState()
class RemoteAccessState : ServiceState()
class RemotePushNotificationState : ServiceState()
class ArmDisarmPushNotificationState : ServiceState()

