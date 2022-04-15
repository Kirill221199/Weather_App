package ru.kirill.weather_app.Repository

interface OnServerResponse {
    fun onResponse(weatherDTO: WeatherDTO)
}