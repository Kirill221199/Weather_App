package ru.kirill.weather_app.Repository

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.kirill.weather_app.BuildConfig
import ru.kirill.weather_app.Other.YANDEX_DOMAIN_HARD_MODE
import ru.kirill.weather_app.Other.convertDtoToModel
import ru.kirill.weather_app.Repository.DTO.WeatherDTO
import ru.kirill.weather_app.viewmodel.DetailsViewModel

class DetailsRepositoryRetrofit2Impl : DetailsRepository {
    override fun getWeatherDetails(city: City, callback: DetailsViewModel.Callback) {
        val weatherApi = Retrofit.Builder().apply {
            baseUrl(YANDEX_DOMAIN_HARD_MODE)
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        }.build().create(WeatherAPI::class.java)
        weatherApi.getWeather(BuildConfig.WEATHER_API_KEY, city.lat, city.lon).enqueue(object :
            Callback<WeatherDTO> {
            override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val weather = convertDtoToModel(it)
                        weather.city = city
                        callback.onResponse(convertDtoToModel(it))
                    }
                } else {
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
                callback.onFailure()
            }

        })
    }
}