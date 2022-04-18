package ru.kirill.weather_app.Repository

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import ru.kirill.weather_app.BuildConfig
import ru.kirill.weather_app.Other.*
import ru.kirill.weather_app.Repository.DTO.WeatherDTO
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DetailsService(val name: String = "") : IntentService(name) {
    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            val lat = it.getDoubleExtra(KEY_BUNDLE_LAT, 0.0)
            val lon = it.getDoubleExtra(KEY_BUNDLE_LON, 0.0)
            Log.d("@@@", "$lat $lon")
            val message = Intent(KEY_WAVE)

            val urlText = "$YANDEX_DOMAIN_HARD_MODE$YANDEX_PATH"+"lat=$lat&lon=$lon"
            val uri = URL(urlText)
            val urlConnection: HttpURLConnection =
                (uri.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 1000
                    readTimeout = 1000
                    addRequestProperty("$YANDEX_API_KEY", BuildConfig.WEATHER_API_KEY)
                }
            try {
                Thread {
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
                        message.putExtra(KEY_BUNDLE_SERVICE_BROADCAST_WEATHER, weatherDTO)
                        LocalBroadcastManager.getInstance(this).sendBroadcast(message)
                    }
                }.start()
            } catch (e: FileNotFoundException) {
                // show e
            } finally {
                urlConnection.disconnect()
            }

        }
    }
}