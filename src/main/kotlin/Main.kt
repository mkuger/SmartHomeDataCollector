import bsh.BSHSubsystem
import mu.KotlinLogging
import netatmo.NetatmoSubsystem
import java.util.concurrent.Executors

fun main(args: Array<String>) {
    val log = KotlinLogging.logger {}
    log.info("Starting Smart Home Data Collector")

    val executor = Executors.newSingleThreadScheduledExecutor()

    BSHSubsystem.start(executor)
    NetatmoSubsystem.start(executor)

    log.info("Setup finished")
    log.debug("Going to sleep...")
    while (true) {
        Thread.sleep(1000 * 60)
    }
}

