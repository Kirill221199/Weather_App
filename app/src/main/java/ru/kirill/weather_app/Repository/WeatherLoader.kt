package ru.kirill.weather_app.Repository

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import ru.kirill.weather_app.BuildConfig
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class WeatherLoader(val onServerResponseListener: OnServerResponse) {

    fun loadWeather(lat: Double, lon: Double) {
        val urlText = "https://api.weather.yandex.ru/v2/informers?lat=$lat&lon=$lon"
        val uri = URL(urlText)
        val urlConnection: HttpsURLConnection =
            (uri.openConnection() as HttpsURLConnection).apply {
                connectTimeout = 1000
                readTimeout = 1000
                addRequestProperty("X-Yandex-API-Key", BuildConfig.WEATHER_API_KEY)
            }
        try {
            Thread {
                val headers = urlConnection.headerFields
                val responseCode = urlConnection.responseCode
                val responseMessage = urlConnection.responseMessage
                val buffer = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val weatherDTO: WeatherDTO = Gson().fromJson(buffer, WeatherDTO::class.java)

                if (responseCode >= 500) {
                    // server
                } else if (responseCode >= 400) {
                    // client
                } else if (responseCode < 300) {
                    Handler(Looper.getMainLooper()).post() {
                        onServerResponseListener.onResponse(weatherDTO)
                    }
                }
            }.start()
        }
        catch (e:Exception){
            // show e
        }
        finally {
            urlConnection.disconnect()
        }
    }
}