package ru.kirill.weather_app.Repository

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import ru.kirill.weather_app.Other.YANDEX_API_KEY
import ru.kirill.weather_app.Other.YANDEX_PATH
import ru.kirill.weather_app.Repository.DTO.WeatherDTO

interface WeatherAPI {
    @GET(YANDEX_PATH)
    fun getWeather(
        @Header(YANDEX_API_KEY) apikey: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Call<WeatherDTO>
}