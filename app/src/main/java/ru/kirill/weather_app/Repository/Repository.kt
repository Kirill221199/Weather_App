package ru.kirill.weather_app.Repository

interface Repository {
    fun getWeatherFromServer(): Weather

    fun getWeatherFromLocalStorageWorld(): List<Weather>

    fun getWeatherFromLocalStorageRus(): List<Weather>
}