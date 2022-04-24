package ru.kirill.weather_app.Repository

import ru.kirill.weather_app.viewmodel.DetailsViewModel

interface DetailsRepository {
    fun getWeatherDetails(city:City, callback: DetailsViewModel.Callback)
}