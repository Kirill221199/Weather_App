package ru.kirill.weather_app.Repository

import ru.kirill.weather_app.Repository.DTO.WeatherDTO

interface OnServerResponse {
    fun onResponse(weatherDTO: WeatherDTO)
}