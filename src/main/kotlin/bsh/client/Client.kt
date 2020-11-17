package bsh.client

import GlobalConfig
import bsh.Device
import bsh.Room
import bsh.Service
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.jackson.responseObject
import smarthome.ConfigHelper
import java.nio.file.Files
import java.security.KeyStore
import javax.net.ssl.*

object Client {
    val fuelManager: FuelManager

    init {
        val config = ConfigHelper.config.bsh
        val truststore = KeyStore.getInstance("JKS")
        truststore.load(javaClass.classLoader.getResourceAsStream("bsh.jks"), "foobar".toCharArray())

        val keystore = KeyStore.getInstance("PKCS12")
        val certFile = ConfigHelper.appHome.resolve("client-cert.p12")
        keystore.load(Files.newInputStream(certFile), config.clientCertPassword.toCharArray())

        val context = SSLContext.getInstance("SSL")
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keystore, config.clientCertPassword.toCharArray())
        val keyManagers = keyManagerFactory.keyManagers
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(truststore)
        val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
        context.init(keyManagers, trustManagers, null)

        fuelManager = FuelManager().apply {
            socketFactory = context.socketFactory
            hostnameVerifier = HostnameVerifier { _: String, _: SSLSession ->
                true
            }
            this.keystore = keystore
            basePath = "https://${config.ip}:${config.port}"
            // 40 Seconds. According to docs, BSH uses 30s for long polling
            timeoutReadInMillisecond = 1000 * 40
        }
    }

    fun rooms(): Array<Room> {
        val url = "/smarthome/rooms"
        val request = fuelManager.get(url)
            .responseObject<Array<Room>>(GlobalConfig.jsonMapper)
        return request.third.get()
    }

    fun devices(): Array<Device> {
        val url = "/smarthome/devices"
        val request = fuelManager.get(url)
            .responseObject<Array<Device>>(GlobalConfig.jsonMapper)
        return request.third.get()
    }

    fun services(): Array<Service> {
        val url = "/smarthome/services"
        val request = fuelManager.get(url)
            .responseObject<Array<Service>>(GlobalConfig.jsonMapper)
        return request.third.get()
    }

    fun servicesByDevice(device: String): Array<Service> {
        val url = "/smarthome/devices/${device}/services"
        val request = fuelManager.get(url)
            .responseObject<Array<Service>>(GlobalConfig.jsonMapper)
        return request.third.get()
    }
}
