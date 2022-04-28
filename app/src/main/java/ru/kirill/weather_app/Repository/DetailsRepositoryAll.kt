package ru.kirill.weather_app.Repository

import ru.kirill.weather_app.viewmodel.HistoryViewModel

interface DetailsRepositoryAll {
    fun getAllWeatherDetails(callback: HistoryViewModel.CallbackForAll)
}