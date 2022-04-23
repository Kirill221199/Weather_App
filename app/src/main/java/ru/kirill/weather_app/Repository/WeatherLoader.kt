package ru.kirill.weather_app.Repository

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import ru.kirill.weather_app.BuildConfig
import ru.kirill.weather_app.Other.YANDEX_API_KEY
import ru.kirill.weather_app.Other.YANDEX_DOMAIN_HARD_MODE
import ru.kirill.weather_app.Other.YANDEX_PATH
import ru.kirill.weather_app.Repository.DTO.WeatherDTO
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class WeatherLoader(val onServerResponseListener: OnServerResponse) {

    fun loadWeather(lat: Double, lon: Double) {
        val urlText = "$YANDEX_DOMAIN_HARD_MODE$YANDEX_PATH+lat=$lat&lon=$lon"
        val uri = URL(urlText)
        val urlConnection: HttpURLConnection =
            (uri.openConnection() as HttpURLConnection).apply {
                connectTimeout = 1000
                readTimeout = 1000
                addRequestProperty("$YANDEX_API_KEY", BuildConfig.WEATHER_API_KEY)
            }
        Thread {
            try {
                val headers = urlConnection.headerFields
                val responseCode = urlConnection.responseCode
                val responseMessage = urlConnection.responseMessage
                val buffer = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val weatherDTO: WeatherDTO = Gson().fromJson(buffer, WeatherDTO::class.java)

                val codeErrorServer = 500
                val codeErrorClient = 400..499
                val codeSuccess = 300

                if (responseCode >= codeErrorServer) {
                    // server
                } else if (responseCode in codeErrorClient) {
                    // client
                } else if (responseCode < codeSuccess) {
                    Handler(Looper.getMainLooper()).post() {
                        onServerResponseListener.onResponse(weatherDTO)
                    }
                }
            } catch (e: FileNotFoundException) {
                // show e
            } finally {
                urlConnection.disconnect()
            }
        }.start()
    }
}