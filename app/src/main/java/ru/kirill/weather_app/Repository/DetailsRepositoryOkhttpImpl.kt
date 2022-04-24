package ru.kirill.weather_app.Repository

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.kirill.weather_app.BuildConfig
import ru.kirill.weather_app.Other.*
import ru.kirill.weather_app.Repository.DTO.WeatherDTO
import ru.kirill.weather_app.viewmodel.DetailsState
import ru.kirill.weather_app.viewmodel.DetailsViewModel

class DetailsRepositoryOkhttpImpl : DetailsRepository {
    override fun getWeatherDetails(city: City, callback: DetailsViewModel.Callback) {
        val client = OkHttpClient()
        val builder = Request.Builder()
        builder.addHeader(YANDEX_API_KEY, BuildConfig.WEATHER_API_KEY)
        builder.url("$YANDEX_DOMAIN_HARD_MODE${YANDEX_PATH}lat=${city.lat}&lon=${city.lon}")
        val request = builder.build()
        val call = client.newCall(request)
        Thread{
            val response = call.execute()
            if(response.isSuccessful){
                val serverResponse = response.body()!!.string()
                val weatherDTO: WeatherDTO = Gson().fromJson(serverResponse, WeatherDTO::class.java)
                val weather = convertDtoToModel(weatherDTO)
                weather.city = city
                callback.onResponse(weather)
            }else{
                callback.onFailure()
            }
        }.start()
    }
}