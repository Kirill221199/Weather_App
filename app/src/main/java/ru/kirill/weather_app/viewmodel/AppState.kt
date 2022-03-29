package ru.kirill.weather_app.viewmodel

import ru.kirill.weather_app.Repository.Weather

sealed class AppState{
    object Loading:AppState()
    data class Success(val weatherData:Weather):AppState()
    data class Error(val error:Throwable):AppState()
}
