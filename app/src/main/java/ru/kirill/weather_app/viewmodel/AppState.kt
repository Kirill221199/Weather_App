package ru.kirill.weather_app.viewmodel

import ru.kirill.weather_app.Repository.Weather

sealed class AppState{
    object Loading:AppState()
    data class Success(val weatherData:List<Weather>):AppState()
    data class Error(val error:Throwable):AppState()
}
