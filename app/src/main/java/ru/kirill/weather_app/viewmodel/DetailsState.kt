package ru.kirill.weather_app.viewmodel

import ru.kirill.weather_app.Repository.Weather

sealed class DetailsState{
    object Loading:DetailsState()
    data class Success(val weatherData:Weather):DetailsState()
    data class Error(val error:Throwable):DetailsState()
}
