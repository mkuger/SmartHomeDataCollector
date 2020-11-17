package netatmo

import GlobalConfig
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.jackson.responseObject
import mu.KotlinLogging
import smarthome.ConfigHelper
import java.time.Instant

fun main() {
    NetatmoPoller.run()
}

object Client {
    private val log = KotlinLogging.logger {}
    private val fuelManager: FuelManager = FuelManager()
    private var token = Token("", "", Instant.MIN)

    init {
        fuelManager.apply {
            basePath = ConfigHelper.config.grafana.url
            basePath = "https://api.netatmo.com/"
        }
    }

    private fun login(): AuthResponse {
        val config = ConfigHelper.config.netatmo
        val url = "oauth2/token"
        val parameters = listOf(
            Headers.CONTENT_TYPE to "application/x-www-form-urlencoded;charset=UTF-8",
            "grant_type" to "password",
            "client_id" to config.clientID,
            "client_secret" to config.clientSecret,
            "username" to config.user,
            "password" to config.password
        )
        val response = fuelManager.post(url, parameters)
            .responseObject<AuthResponse>(GlobalConfig.jsonMapper)
        log.debug("${response.second.statusCode} : ${response.second.responseMessage}")
        return response.third.get()
    }

    private fun refreshToken(): AuthResponse {
        val config = ConfigHelper.config.netatmo
        val url = "oauth2/token"
        val parameters = listOf(
            Headers.CONTENT_TYPE to "application/x-www-form-urlencoded;charset=UTF-8",
            "grant_type" to "refresh_token",
            "client_id" to config.clientID,
            "client_secret" to config.clientSecret,
            "refresh_token" to token.refreshToken
        )
        val response = fuelManager.post(url, parameters)
            .responseObject<AuthResponse>(GlobalConfig.jsonMapper)

        log.debug("${response.second.statusCode} : ${response.second.responseMessage}")
        return response.third.get()
    }

    fun queryStation(): Station {
        checkToken()
        val url = "api/getstationsdata"
        val params = listOf("get_favorites" to "true")
        val response = fuelManager.get(url, params)
            .authentication()
            .bearer(token.accessToken)
            .header(Headers.ACCEPT to "application/json")
            .responseObject<StationResponse>(GlobalConfig.jsonMapper)
        log.debug("Station query: ${response.second.statusCode} - ${response.second.responseMessage}")
        return response.third.get().body.devices[0]
    }

    private fun checkToken() {
        val now = Instant.now()
        // token is still valid
        if (token.expires.isAfter(now)) {
            return
        }
        token = if (token.refreshToken == "") {
            // no refresh token - grant flow
            val loginToken = login()
            val expires = now.plusSeconds(loginToken.expires_in!!)
            Token(loginToken.access_token!!, loginToken.refresh_token, expires)
        } else {
            // refresh token
            val refreshedToken = refreshToken()
            val expires = now.plusSeconds(refreshedToken.expires_in!!)
            Token(refreshedToken.access_token!!, refreshedToken.refresh_token, expires)
        }
    }
}