package ru.kirill.weather_app.Repository

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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

            val urlText = "$YANDEX_DOMAIN_HARD_MODE$YANDEX_PATH"+"lat=$lat&lon=$lon"
            val uri = URL(urlText)
            val urlConnection: HttpURLConnection =
                (uri.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 1000
                    readTimeout = 1000
                    addRequestProperty(YANDEX_API_KEY, BuildConfig.WEATHER_API_KEY)
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

                    when {
                        responseCode >= codeErrorServer -> {
                            // server
                            Log.d("@@@", "server")
                            answerService(null)
                        }
                        responseCode in codeErrorClient -> {
                            // client
                            Log.d("@@@", "client")
                            answerService(null)
                        }
                        responseCode < codeSuccess -> {
                            answerService(weatherDTO)
                        }
                    }

                } catch (e: JsonSyntaxException) {
                    // show e
                    Log.d("@@@", "JsonSyntaxException")
                    answerService(null)
                }
                catch (e: FileNotFoundException) {
                    // show e
                    Log.d("@@@", "FileNotFoundException")
                    answerService(null)
                }finally {
                    urlConnection.disconnect()
                }
            }.start()
        }
    }
    private fun answerService(weatherDTO: WeatherDTO?) {
        val message = Intent(KEY_WAVE)
        message.putExtra(
            KEY_BUNDLE_SERVICE_BROADCAST_WEATHER, weatherDTO
        )
        LocalBroadcastManager.getInstance(this).sendBroadcast(message)
    }
}