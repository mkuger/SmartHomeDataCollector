import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection

class EventListener {

    fun call() {

        HttpsURLConnection.getDefaultHostnameVerifier().

        FuelManager()
            .client

        val httpsAsync = "https://192.168.178.55:8446/smarthome/public/information"
            .httpGet()
            .responseString { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        println(ex)
                    }
                    is Result.Success -> {
                        val data = result.get()
                        println(data)
                    }
                }
            }
        httpsAsync.join()
    }
}